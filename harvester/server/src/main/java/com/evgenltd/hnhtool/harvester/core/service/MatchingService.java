package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.KnownItem;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.KnownItemRepository;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
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
public class MatchingService {

    private static final Logger log = LogManager.getLogger(MatchingService.class);

    private KnownObjectRepository knownObjectRepository;
    private KnownItemRepository knownItemRepository;

    public MatchingService(final KnownObjectRepository knownObjectRepository) {
        this.knownObjectRepository = knownObjectRepository;
    }

    BiMap<Long, Long> matchObjects(final List<Prop> props) {

        final List<Prop> filteredProps = filterProps(props);
        if (filteredProps.isEmpty()) {
            log.info("No objects to match");
            return HashBiMap.create();
        }

        final KnownObject offset = new OffsetSeeker(filteredProps).seek();
        if (offset == null) {
            return HashBiMap.create();
        }

        return matchObjects(filteredProps, offset);

    }

    public BiMap<Long, Integer> matchItems(final Long knownObjectId, final List<ItemWidget> items) {

        if (items.isEmpty()) {
            log.info("No items to match");
            return HashBiMap.create();
        }

        final Optional<KnownObject> ownerHolder = knownObjectRepository.findById(knownObjectId);
        if (!ownerHolder.isPresent()) {
            throw new ApplicationException("Container [%s] does not exists", knownObjectId);
        }

        final KnownObject owner = ownerHolder.get();
        final Map<IntPoint, List<KnownItem>> knownItemIndex = loadKnownItemsByOwnerId(knownObjectId);
        final HashBiMap<Long, Integer> resultIndex = HashBiMap.create();

        for (final ItemWidget item : items) {
            final List<KnownItem> matchedKnownItems = knownItemIndex.remove(item.getPosition());
            matchedKnownItems.sort(
                    Comparator.comparing(KnownItem::getLost)
                            .reversed()
                            .thenComparing(KnownItem::getActual)
                            .reversed()
            );

            KnownItem matchedKnownItem;
            if (matchedKnownItems.isEmpty()) {
                matchedKnownItem = storeNewKnownItem(item, owner);
            } else {
                matchedKnownItem = matchedKnownItems.get(0);
                matchedKnownItems.forEach(knownItemRepository::delete);
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

    // ##################################################
    // #                                                #
    // #  Offset Seeker                                 #
    // #                                                #
    // ##################################################

    private List<Prop> filterProps(final List<Prop> props) {
        return props;
    }

    class OffsetSeeker {

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
                dummy.setOwner(foundObject.getOwner());
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

    private BiMap<Long, Long> matchObjects(final List<Prop> props, final KnownObject offset) {

        final Map<String, List<KnownObject>> knownObjectIndex = loadKnownObjectsByPropRange(props, offset);
        final HashBiMap<Long, Long> resultIndex = HashBiMap.create();

        for (final Prop prop : props) {
            final IntPoint adjustedPosition = prop.getPosition().add(offset.getPosition());
            final List<KnownObject> matchedKnownObjects = knownObjectIndex.remove(adjustedPosition + " " + prop.getResource());
            matchedKnownObjects.sort(
                    Comparator.comparing(KnownObject::getLost)
                            .reversed()
                            .thenComparing(KnownObject::getActual)
                            .reversed()
            );

            KnownObject matchedKnownObject;
            if (matchedKnownObjects.isEmpty()) {
                matchedKnownObject = storeNewKnownObject(prop, adjustedPosition, offset.getOwner());
            } else {
                matchedKnownObject = matchedKnownObjects.remove(0);
                matchedKnownObjects.forEach(knownObjectRepository::delete);
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
                offset.getOwner(),
                upperLeft.getX(),
                upperLeft.getY(),
                lowerRight.getX(),
                lowerRight.getY()
        ).stream()
                .collect(Collectors.groupingBy(knownObject -> knownObject.getPosition() + " " + knownObject.getResource()));
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
        final KnownObject knownObject = new KnownObject();
        knownObject.setOwner(space);
        knownObject.setResource(prop.getResource());
        knownObject.setX(adjustedPosition.getX());
        knownObject.setX(adjustedPosition.getY());
        // classification
        return knownObjectRepository.save(knownObject);
    }

    // ##################################################
    // #                                                #
    // #  Item Matcher                                  #
    // #                                                #
    // ##################################################

    private Map<IntPoint, List<KnownItem>> loadKnownItemsByOwnerId(final Long ownerId) {
        return knownItemRepository.findByOwnerId(ownerId)
                .stream()
                .collect(Collectors.groupingBy(KnownItem::getPosition));
    }

    private KnownItem storeNewKnownItem(final ItemWidget item, final KnownObject owner) {
        final KnownItem knownItem = new KnownItem();
        knownItem.setOwner(owner);
        knownItem.setResource(item.getResource());
        knownItem.setX(item.getPosition().getX());
        knownItem.setY(item.getPosition().getY());
        // sort of classification
        return knownItem;
    }

}
