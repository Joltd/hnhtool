package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.component.matcher.Matcher;
import com.evgenltd.hnhtool.harvester.core.component.matcher.MatchingResult;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.entity.WorldPoint;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;
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

    private SpaceRepository spaceRepository;
    private KnownObjectRepository knownObjectRepository;
    private KnownObjectService knownObjectService;
    private ResourceService resourceService;

    public MatchingService(
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository,
            final KnownObjectService knownObjectService,
            final ResourceService resourceService
    ) {
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
        this.knownObjectService = knownObjectService;
        this.resourceService = resourceService;
    }

    public WorldPoint researchObjects(final List<Prop> props, final Predicate<Resource> resourceCondition) {

        final Map<String, Resource> resources = resourceService.loadResourceIndexByNames(
                props,
                Prop::getResource
        );
        resources.values().forEach(resource -> resource.setProp(true));
        final List<Prop> filteredProps = props.stream()
                .filter(prop -> {
                    final Resource resource = resources.get(prop.getResource());
                    return resourceCondition.test(resource) && resource != null;
                })
                .collect(Collectors.toList());

        WorldPoint offset = new OffsetSeeker(filteredProps).seek();
        if (offset == null) {
            offset = storeNewSpace();
        }

        final Area area = new Area(filteredProps, offset.getPosition());
        final List<KnownObject> knownObjects = loadKnownObjects(area, offset.getSpace());
        final MatchingResult<Prop, KnownObject> matchingResult = Matcher.matchPropToKnownObject(
                filteredProps,
                knownObjects
        );

        matchingResult.getMatches().forEach(entry -> {
            entry.getRight().setLost(false);
            entry.getRight().setActual(LocalDateTime.now());
        });
        matchingResult.getRightNotMatched().forEach(knownObject -> knownObject.setLost(true));

        for (final Prop prop : matchingResult.getLeftNotMatched()) {
            knownObjectService.storeNewKnownObject(
                    offset.getSpace(),
                    resources.get(prop.getResource()),
                    prop.getPosition().add(offset.getPosition())
            );
        }

        return offset;
    }

    public void researchItems(final Long parentId, final KnownObject.Place place, final List<ItemWidget> itemWidgets) {

        final KnownObject parent = knownObjectService.findById(parentId);
        final List<KnownObject> knownItems = knownObjectRepository.findByParentIdAndPlace(parentId, place);

        final MatchingResult<ItemWidget, KnownObject> matchingResult = Matcher.matchItemWidgetToKnownObject(
                itemWidgets,
                knownItems
        );

        knownObjectService.storeNewKnownItems(parent, place, matchingResult.getLeftNotMatched());

        matchingResult.getRightNotMatched().forEach(knownObjectRepository::delete);

    }

    private WorldPoint storeNewSpace() {
        final Space space = new Space();
        space.setType(Space.Type.SURFACE);
        space.setName("Ground");
        spaceRepository.save(space);

        final WorldPoint worldPoint = new WorldPoint();
        worldPoint.setSpace(space);
        worldPoint.setPosition(new IntPoint());
        return worldPoint;
    }

    // ##################################################
    // #                                                #
    // #  Known Object loader                           #
    // #                                                #
    // ##################################################

    private List<KnownObject> loadKnownObjects(final Area area, final Space space) {
        return knownObjectRepository.findObjectsInArea(
                space,
                area.getUpperLeftX(),
                area.getUpperLeftY(),
                area.getLowerRightX(),
                area.getLowerRightY()
        );
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
        WorldPoint seek() {

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

                final IntPoint offset = foundObject.getPosition().sub(firstProp.getPosition());

                final WorldPoint worldPoint = new WorldPoint();
                worldPoint.setSpace(foundObject.getSpace());
                worldPoint.setPosition(offset);
                return worldPoint;

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
    // #  Known Object area                             #
    // #                                                #
    // ##################################################

    private static final class Area {

        private IntPoint upperLeft;
        private IntPoint lowerRight;

        Area(final List<Prop> props, final IntPoint offset) {
            upperLeft = new IntPoint(
                    reduceProps(props, IntPoint::getX, Math::min),
                    reduceProps(props, IntPoint::getY, Math::min)
            ).add(offset);
            lowerRight = new IntPoint(
                    reduceProps(props, IntPoint::getX, Math::max),
                    reduceProps(props, IntPoint::getY, Math::max)
            ).add(offset);
        }

        private int reduceProps(final List<Prop> props, final ToIntFunction<IntPoint> pointToInt, final IntBinaryOperator reducer) {
            return props.stream()
                    .map(Prop::getPosition)
                    .mapToInt(pointToInt)
                    .reduce(reducer)
                    .orElse(0);
        }

        int getUpperLeftX() {
            return upperLeft.getX();
        }

        int getUpperLeftY() {
            return upperLeft.getY();
        }

        int getLowerRightX() {
            return lowerRight.getX();
        }

        int getLowerRightY() {
            return lowerRight.getY();
        }

    }

}
