package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester.common.component.ObjectIndex;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.common.repository.SpaceRepository;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.entity.WorldObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 31-03-2019 13:13</p>
 */
@Service
public class KnowledgeMatchingService {

    private static final Logger log = LogManager.getLogger(KnowledgeMatchingService.class);

    private SpaceRepository spaceRepository;
    private KnownObjectRepository knownObjectRepository;

    public KnowledgeMatchingService(
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository
    ) {
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
    }

    public ObjectIndex match(
            @Nullable final ObjectIndex oldIndex,
            @NotNull final WorldObject woCharacter,
            @NotNull KnownObject koCharacter,
            final List<WorldObject> objects
    ) {

        final ObjectIndex objectIndex = new ObjectIndex();
        if (objects == null || objects.isEmpty()) {
            return objectIndex;
        }

        Assert.valueRequireNonEmpty(woCharacter, "Character");

        objectIndex.putMatch(koCharacter.getId(), woCharacter.getId());

        final List<WorldObject> objectsForMatching = filterWasteObjects(objects);

        final IntPoint upperLeft = woCharacter.getPosition().add(-50000, -50000);
        final IntPoint lowerRight = woCharacter.getPosition().add(50000, 50000);

        final List<KnownObject> knownObjects = knownObjectRepository.findObjectsInArea(
                koCharacter.getOwner(),
                upperLeft.getX(),
                upperLeft.getY(),
                lowerRight.getX(),
                lowerRight.getY()
        );

        for (final KnownObject knownObject : knownObjects) {

            final Long matchedWorldObjectId = getMatchedWorldObjectId(oldIndex, knownObject);

            final WorldObject matchedWorldObject = lookupMatchedObject(objectsForMatching, knownObject, matchedWorldObjectId);
            if (matchedWorldObject == null) {
                continue;
            }

            knownObject.setActual(LocalDateTime.now());
            knownObjectRepository.save(knownObject);

            objectIndex.putMatch(knownObject.getId(), matchedWorldObject.getId());

            objectsForMatching.remove(matchedWorldObject);

        }

        // here objectsForMatching contains only unknown worldObjects

        for (final WorldObject worldObject : objectsForMatching) {

            if (Objects.equals(worldObject.getResourceId(), ResourceConstants.PLAYER)) {
                continue;
            }

            final KnownObject knownObject = rememberWorldObject(koCharacter.getOwner(), worldObject);
            objectIndex.putMatch(knownObject.getId(), worldObject.getId());
        }

        return objectIndex;
    }

    private List<WorldObject> filterWasteObjects(final List<WorldObject> objects) {
        return objects.stream()
                .filter(wo -> !ResourceConstants.isWaste(wo.getResourceId()))
                .collect(Collectors.toList());
    }

    private WorldObject lookupMatchedObject(
            final List<WorldObject> worldObjects,
            final KnownObject knownObject,
            final Long matchedPreviousWorldObjectId
    ) {
        for (final WorldObject worldObject : worldObjects) {
            if (Objects.equals(matchedPreviousWorldObjectId, worldObject.getId())) {
                return worldObject;
            }

            if (!Objects.equals(worldObject.getPosition(), knownObject.getPosition())) {
                continue;
            }

            if (Objects.equals(knownObject.getResourceId(), worldObject.getResourceId())) {
                return worldObject;
            }
        }

        return null;
    }

    public KnownObject rememberCharacterObject(final WorldObject worldObject) {
        final Space space = spaceRepository.findByType(Space.Type.SURFACE)
                .orElseGet(this::rememberSurfaceSpace);
        return rememberWorldObject(space, worldObject);
    }

    private Space rememberSurfaceSpace() {
        final Space space = new Space();
        space.setName("Surface");
        space.setType(Space.Type.SURFACE);
        return spaceRepository.save(space);
    }

    private KnownObject rememberWorldObject(final Space space, final WorldObject worldObject) {
        final KnownObject knownObject = new KnownObject();
        knownObject.setOwner(space);
        knownObject.setResourceId(worldObject.getResourceId());
        knownObject.setX(worldObject.getPosition().getX());
        knownObject.setY(worldObject.getPosition().getY());
        knownObject.setActual(LocalDateTime.now());
        objectClassification(knownObject);
        return knownObjectRepository.save(knownObject);
    }

    private void objectClassification(final KnownObject knownObject) {
        final Long resourceId = knownObject.getResourceId();
        if (ResourceConstants.isPlayer(resourceId)) {
            knownObject.setPlayer(true);
        } else if (ResourceConstants.isDoorway(resourceId)) {
            knownObject.setDoorway(true);
        } else if (ResourceConstants.isContainer(resourceId)) {
            knownObject.setContainer(true);
        }
    }

    private Long getMatchedWorldObjectId(final ObjectIndex oldIndex, final KnownObject knownObject) {
        if (oldIndex == null) {
            return null;
        }
        return oldIndex.getMatchedWorldObjectId(knownObject.getId())
                .getValue();
    }

}
