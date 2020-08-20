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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchingService {

    private static final Logger log = LogManager.getLogger(MatchingService.class);

    private final SpaceRepository spaceRepository;
    private final KnownObjectRepository knownObjectRepository;
    private final KnownObjectService knownObjectService;
    private final ResourceService resourceService;

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
        resources.values().forEach(resource -> resource.setVisual(Resource.Visual.PROP));
        final List<Prop> filteredProps = props.stream()
                .filter(prop -> {
                    final Resource resource = resources.get(prop.getResource());
                    return resourceCondition.test(resource) && resource != null && Assert.isNotEmpty(prop.getResource());
                })
                .collect(Collectors.toList());

        WorldPoint offset = seekOffset(filteredProps);
        if (offset == null) {
            offset = storeNewSpace();
        }

        final Area area = new Area(filteredProps, offset.getPosition());
        final List<KnownObject> knownObjects = loadKnownObjects(area, offset.getSpaceId());
        final MatchingResult<Prop, KnownObject> matchingResult = Matcher.matchPropToKnownObject(
                filteredProps,
                knownObjects,
                offset.getPosition()
        );

        matchingResult.getMatches().forEach(entry -> {
            entry.getRight().setLost(false);
            entry.getRight().setActual(LocalDateTime.now());
        });
        matchingResult.getRightNotMatched().forEach(knownObject -> knownObject.setLost(true));

        for (final Prop prop : matchingResult.getLeftNotMatched()) {
            knownObjectService.storeNewKnownObject(
                    offset.getSpaceId(),
                    resources.get(prop.getResource()),
                    prop.getPosition().add(offset.getPosition())
            );
        }

        return offset;
    }

    public void researchItems(final Long parentId, final KnownObject.Place place, final List<ItemWidget> itemWidgets) {

        final KnownObject parent = knownObjectRepository.getOne(parentId);
        final List<KnownObject> knownItems = knownObjectRepository.findByParentIdAndPlace(parentId, place);

        final MatchingResult<ItemWidget, KnownObject> matchingResult = Matcher.matchItemWidgetToKnownObject(
                itemWidgets,
                knownItems
        );

        knownObjectService.storeNewKnownItems(parent, place, matchingResult.getLeftNotMatched());

        matchingResult.getRightNotMatched().forEach(knownObjectRepository::delete);

    }

    @Nullable
    public Long researchHand(final Long parentId, @Nullable final ItemWidget itemWidget) {

        final List<ItemWidget> itemWidgets = Optional.ofNullable(itemWidget)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
        final KnownObject parent = knownObjectRepository.getOne(parentId);
        final List<KnownObject> knownItems = knownObjectRepository.findByParentIdAndPlace(parentId, KnownObject.Place.HAND);

        final MatchingResult<ItemWidget, KnownObject> matchingResult = Matcher.matchItemWidgetToKnownObject(
                itemWidgets,
                knownItems
        );

        if (!matchingResult.getRightNotMatched().isEmpty()) {
            knownObjectRepository.delete(matchingResult.getRightNotMatched().get(0));
        }

        if (itemWidget == null) {
            return null;
        }

        if (!matchingResult.getMatches().isEmpty()) {
            return matchingResult.getMatches().get(0).getRight().getId();
        }

        final KnownObject knownItem = knownObjectService.storeNewKnownItem(
                parent,
                KnownObject.Place.HAND,
                itemWidget
        );
        return knownItem.getId();
    }

    private WorldPoint storeNewSpace() {
        final Space space = new Space();
        space.setType(Space.Type.SURFACE);
        space.setName("Ground");
        spaceRepository.save(space);

        final WorldPoint worldPoint = new WorldPoint();
        worldPoint.setSpaceId(space.getId());
        worldPoint.setPosition(new IntPoint());
        return worldPoint;
    }

    // ##################################################
    // #                                                #
    // #  Known Object loader                           #
    // #                                                #
    // ##################################################

    private List<KnownObject> loadKnownObjects(final Area area, final Long spaceId) {
        return knownObjectRepository.findObjectsInArea(
                spaceId,
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

    private WorldPoint seekOffset(final List<Prop> props) {
        for (int firstIndex = 0; firstIndex < props.size(); firstIndex++) {
            for (int secondIndex = firstIndex + 1; secondIndex < props.size(); secondIndex++) {
                final Prop firstProp = props.get(firstIndex);
                final Prop secondProp = props.get(secondIndex);

                final KnownObject foundObject = findFirstObjectByPattern(firstProp, secondProp);
                if (foundObject == null) {
                    continue;
                }

                final IntPoint offset = foundObject.getPosition().sub(firstProp.getPosition());

                final WorldPoint worldPoint = new WorldPoint();
                worldPoint.setSpaceId(foundObject.getSpace().getId());
                worldPoint.setPosition(offset);
                return worldPoint;
            }
        }

        return null;
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

    // ##################################################
    // #                                                #
    // #  Known Object area                             #
    // #                                                #
    // ##################################################

    private static final class Area {

        private final IntPoint upperLeft;
        private final IntPoint lowerRight;

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
