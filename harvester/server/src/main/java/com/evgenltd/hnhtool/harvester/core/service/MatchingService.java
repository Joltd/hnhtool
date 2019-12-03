package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceRepository;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 27-11-2019 23:03</p>
 */
@Service
@Transactional
public class MatchingService {

    private static final Logger log = LogManager.getLogger(MatchingService.class);

    private ResourceRepository resourceRepository;
    private SpaceRepository spaceRepository;
    private KnownObjectRepository knownObjectRepository;

    public MatchingService(
            final ResourceRepository resourceRepository,
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository
    ) {
        this.resourceRepository = resourceRepository;
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
    }

    public BiMap<Long, Long> matchObjects(final List<Prop> props, final String characterName, final Prop characterProp) {

        final List<Prop> filteredProps = filterProps(props);
        if (filteredProps.isEmpty()) {
            log.info("No objects to match");
            return HashBiMap.create();
        }

        KnownObject offset = new OffsetSeeker(filteredProps).seek();
        if (offset == null) {
            offset = storeNewSpace();
        }

        final BiMap<Long, Long> resultIndex = matchObjects(filteredProps, offset);

        final KnownObject characterObject = updateCharacterObject(characterName, characterProp, offset);
        resultIndex.put(characterObject.getId(), characterProp.getId());

        return resultIndex;

    }

    public BiMap<Long, Integer> matchItems(final Long knownObjectId, final KnownObject.Place place, final List<ItemWidget> items) {

        if (items.isEmpty()) {
            log.info("No items to match");
            return HashBiMap.create();
        }

        final Optional<KnownObject> ownerHolder = knownObjectRepository.findById(knownObjectId);
        if (!ownerHolder.isPresent()) {
            throw new ApplicationException("Container [%s] does not exists", knownObjectId);
        }

        final KnownObject owner = ownerHolder.get();
        final Map<IntPoint, List<KnownObject>> knownItemIndex = loadKnownItemsByOwnerId(knownObjectId, place);
        final HashBiMap<Long, Integer> resultIndex = HashBiMap.create();

        for (final ItemWidget item : items) {
            List<KnownObject> matchedKnownItems = knownItemIndex.remove(item.getPosition());
            if (matchedKnownItems == null) {
                matchedKnownItems = new ArrayList<>();
            }
            matchedKnownItems.sort(
                    Comparator.comparing(KnownObject::getLost)
                            .reversed()
                            .thenComparing(KnownObject::getActual)
                            .reversed()
            );

            KnownObject matchedKnownItem;
            if (matchedKnownItems.isEmpty()) {
                matchedKnownItem = storeNewKnownItem(item, owner, place);
            } else {
                matchedKnownItem = matchedKnownItems.remove(0);
//                matchedKnownItems.forEach(knownObjectRepository::delete);
            }

            matchedKnownItem.setActual(LocalDateTime.now());
            matchedKnownItem.setLost(false);
            resultIndex.put(matchedKnownItem.getId(), item.getId());
        }

        knownItemIndex.values()
                .stream()
                .flatMap(Collection::stream)
                .forEach(knownItem -> knownItem.setLost(true));

        return resultIndex;
    }

    private List<Prop> filterProps(final List<Prop> props) {
        return props.stream()
                .filter(prop -> Assert.isNotEmpty(prop.getResource()))
                .collect(Collectors.toList());
    }

    private KnownObject storeNewSpace() {
        final Space space = new Space();
        space.setType(Space.Type.SURFACE);
        space.setName("Ground");
        spaceRepository.save(space);

        final KnownObject dummy = new KnownObject();
        dummy.setSpace(space);
        dummy.setX(0);
        dummy.setY(0);
        return dummy;
    }

    // ##################################################
    // #                                                #
    // #  Offset Seeker                                 #
    // #                                                #
    // ##################################################

    private class OffsetSeeker {

        private final List<Prop> props;
        private final List<String> excludedResources = new ArrayList<>();

        OffsetSeeker(final List<Prop> props) {
            this.props = new ArrayList<>(props);
        }

        @Nullable
        KnownObject seek() {

            while (true) {

                excludedResources.clear();
                final Prop firstProp = selectRandomProp();
                final Prop secondProp = selectRandomProp();
                if (firstProp == null || secondProp == null) {
                    log.info("Props are over");
                    return null;
                }

                final KnownObject foundObject = findFirstObjectByPattern(firstProp, secondProp);
                if (foundObject == null) {
                    continue;
                }

                final KnownObject dummy = new KnownObject();
                dummy.setX(foundObject.getX() - firstProp.getPosition().getX());
                dummy.setY(foundObject.getY() - firstProp.getPosition().getY());
                dummy.setSpace(foundObject.getSpace());
                return dummy;

            }

        }

        @Nullable
        private Prop selectRandomProp() {
            return props.stream()
                    .filter(this::isResourceNotRestricted)
                    .findAny()
                    .map(prop -> {
                        props.remove(prop);
                        excludedResources.add(prop.getResource());
                        return prop;
                    })
                    .orElse(null);
        }

        private boolean isResourceNotRestricted(final Prop prop) {
            return Assert.isNotEmpty(prop.getResource())
                    && !excludedResources.contains(prop.getResource());
        }

        private KnownObject findFirstObjectByPattern(final Prop first, final Prop second) {
            final List<KnownObject> suitableCandidates = knownObjectRepository.findObjectByPattern(
                    first.getResource(),
                    first.getPosition().getX(),
                    first.getPosition().getY(),
                    second.getResource(),
                    second.getPosition().getX(),
                    second.getPosition().getY()
            );

            if (suitableCandidates.isEmpty()) {
                log.info("No suitable object candidates");
                return null;
            }

            if (suitableCandidates.size() > 1) {
                log.info("Too much object candidates, [{}]", suitableCandidates.size());
            }

            return suitableCandidates.get(0);
        }

    }

    // ##################################################
    // #                                                #
    // #  Prop Object Matcher                           #
    // #                                                #
    // ##################################################

    private KnownObject updateCharacterObject(final String characterName, final Prop characterProp, final KnownObject dummy) {
        final KnownObject characterObject = knownObjectRepository.findCharacterObject(characterName)
                .orElseThrow(() -> new ApplicationException("There is no known object for [%s]", characterName));

        characterObject.setX(characterProp.getPosition().getX());
        characterObject.setY(characterProp.getPosition().getY());
        characterObject.setSpace(dummy.getSpace());
        characterObject.setActual(LocalDateTime.now());
        return characterObject;
    }

    private BiMap<Long, Long> matchObjects(final List<Prop> props, final KnownObject offset) {

        final Map<String, List<KnownObject>> knownObjectIndex = loadKnownObjectsByPropRange(props, offset);
        final HashBiMap<Long, Long> resultIndex = HashBiMap.create();

        for (final Prop prop : props) {
            final IntPoint adjustedPosition = prop.getPosition().add(offset.getPosition());
            List<KnownObject> matchedKnownObjects = knownObjectIndex.remove(adjustedPosition + " " + prop.getResource());
            if (matchedKnownObjects == null) {
                matchedKnownObjects = new ArrayList<>();
            }
            matchedKnownObjects.sort(
                    Comparator.comparing(KnownObject::getLost)
                            .reversed()
                            .thenComparing(KnownObject::getActual)
                            .reversed()
            );

            KnownObject matchedKnownObject;
            if (matchedKnownObjects.isEmpty()) {
                matchedKnownObject = storeNewKnownObject(prop, adjustedPosition, offset.getSpace());
            } else {
                matchedKnownObject = matchedKnownObjects.remove(0);
//                matchedKnownObjects.forEach(knownObjectRepository::delete);
            }

            matchedKnownObject.setActual(LocalDateTime.now());
            matchedKnownObject.setLost(false);
            resultIndex.put(matchedKnownObject.getId(), prop.getId());
        }

        knownObjectIndex.values()
                .stream()
                .flatMap(Collection::stream)
                .forEach(knownObject -> knownObject.setLost(true));

        return resultIndex;
    }

    private Map<String, List<KnownObject>> loadKnownObjectsByPropRange(final List<Prop> props, final KnownObject offset) {
        final IntPoint upperLeft = new IntPoint(
                reduceProps(props, IntPoint::getX, Math::min),
                reduceProps(props, IntPoint::getY, Math::min)
        );
        final IntPoint lowerRight = new IntPoint(
                reduceProps(props, IntPoint::getX, Math::max),
                reduceProps(props, IntPoint::getY, Math::max)
        );

        return knownObjectRepository.findObjectsInArea(
                offset.getSpace(),
                upperLeft.getX(),
                upperLeft.getY(),
                lowerRight.getX(),
                lowerRight.getY()
        ).stream()
                .collect(Collectors.groupingBy(knownObject -> knownObject.getPosition() + " " + knownObject.getResource().getName()));
    }

    private int reduceProps(final List<Prop> props, final ToIntFunction<IntPoint> pointToInt, final IntBinaryOperator reducer) {
        return props.stream()
                .map(Prop::getPosition)
                .mapToInt(pointToInt)
                .reduce(reducer)
                .orElse(0);
    }

    private KnownObject storeNewKnownObject(
            final Prop prop,
            final IntPoint adjustedPosition,
            final Space space
    ) {
        final Resource resource = resourceRepository.findAndCreateIfNecessary(prop.getResource());
        resource.setProp(true);
        final KnownObject knownObject = new KnownObject();
        knownObject.setSpace(space);
        knownObject.setResource(resource);
        knownObject.setX(adjustedPosition.getX());
        knownObject.setY(adjustedPosition.getY());
        return knownObjectRepository.save(knownObject);
    }

    // ##################################################
    // #                                                #
    // #  Item Matcher                                  #
    // #                                                #
    // ##################################################

    private Map<IntPoint, List<KnownObject>> loadKnownItemsByOwnerId(
            final Long parentId,
            final KnownObject.Place place
    ) {
        return knownObjectRepository.findByParentIdAndPlace(parentId, place)
                .stream()
                .collect(Collectors.groupingBy(KnownObject::getPosition));
    }

    private KnownObject storeNewKnownItem(final ItemWidget item, final KnownObject parent, final KnownObject.Place place) {
        final Resource resource = resourceRepository.findAndCreateIfNecessary(item.getResource());
        final KnownObject knownItem = new KnownObject();
        knownItem.setParent(parent);
        knownItem.setPlace(place);
        knownItem.setResource(resource);
        knownItem.setX(item.getPosition().getX());
        knownItem.setY(item.getPosition().getY());
        return knownObjectRepository.save(knownItem);
    }

}
