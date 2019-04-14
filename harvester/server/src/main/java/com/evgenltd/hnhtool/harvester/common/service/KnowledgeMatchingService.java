package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester.common.component.ObjectIndex;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.ServerResultCode;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.common.repository.SpaceRepository;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.entity.WorldObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static final List<Long> RESTRICTED_OBJECT_RESOURCES = Arrays.asList(
            0L
    );

    private SpaceRepository spaceRepository;
    private KnownObjectRepository knownObjectRepository;

    public KnowledgeMatchingService(
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository
    ) {
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
    }

    public Result<ObjectIndex> match(
            @Nullable final ObjectIndex oldIndex,
            @NotNull final WorldObject woCharacter,
            @NotNull KnownObject koCharacter,
            final List<WorldObject> objects
    ) {

        final ObjectIndex objectIndex = new ObjectIndex();
        if (objects == null || objects.isEmpty()) {
            return Result.fail(ServerResultCode.KMS_WORLD_OBJECTS_NOT_PRESENTED);
        }

        Assert.valueRequireNonEmpty(woCharacter, "Character");

        objectIndex.putMatch(koCharacter.getId(), woCharacter.getId());

        final List<WorldObject> objectsForMatching = filterWasteObjects(objects);
        if (objectsForMatching.isEmpty()) {
            return Result.fail(ServerResultCode.KMS_WORLD_OBJECTS_ALL_FILTERED);
        }

        final Result<KnownObjectRepository.SpaceInfo> referencePoint = findReferencePoint(objectsForMatching);
        if (referencePoint.isFailed()) {
            return referencePoint.cast();
        }

        objectIndex.putOffset(
                referencePoint.getValue().getX(),
                referencePoint.getValue().getY()
        );

        koCharacter.setX(woCharacter.getPosition().getX() + referencePoint.getValue().getX());
        koCharacter.setY(woCharacter.getPosition().getY() + referencePoint.getValue().getY());
        spaceRepository.findById(referencePoint.getValue().getSpaceId())
                .ifPresent(koCharacter::setOwner); // todo repo should operate with particular Space instead of Id
        knownObjectRepository.save(koCharacter);

        final IntPoint upperLeft = koCharacter.getPosition().add(-50000, -50000);
        final IntPoint lowerRight = koCharacter.getPosition().add(50000, 50000);

        final List<KnownObject> knownObjects = knownObjectRepository.findObjectsInArea(
                koCharacter.getOwner(),
                upperLeft.getX(),
                upperLeft.getY(),
                lowerRight.getX(),
                lowerRight.getY()
        );

        for (final WorldObject worldObject : objectsForMatching) {

            if (Objects.equals(worldObject.getResourceId(), ResourceConstants.PLAYER)) {
                continue;
            }

            if (oldIndex != null) {
                final Result<Long> matchedKnownObjectId = oldIndex.getMatchedKnownObjectId(worldObject.getId());
                if (matchedKnownObjectId.isSuccess()) {
                    objectIndex.putMatch(matchedKnownObjectId.getValue(), worldObject.getId());
                    continue;
                }
            }

            KnownObject matchedKnownObject = lookupMatchedObject(knownObjects, worldObject, objectIndex.getOffset());
            if (matchedKnownObject == null) {
                matchedKnownObject = rememberWorldObject(koCharacter.getOwner(), worldObject);
            } else {
                matchedKnownObject.setActual(LocalDateTime.now());
                knownObjectRepository.save(matchedKnownObject);
            }

            objectIndex.putMatch(matchedKnownObject.getId(), matchedKnownObject.getId());

            knownObjects.remove(matchedKnownObject);

        }

        return Result.ok(objectIndex);
    }

    private List<WorldObject> filterWasteObjects(final List<WorldObject> objects) {
        return objects.stream()
                .filter(wo -> !ResourceConstants.isWaste(wo.getResourceId()))
                .collect(Collectors.toList());
    }

    private KnownObject lookupMatchedObject(
            final List<KnownObject> knownObjects,
            final WorldObject worldObject,
            final IntPoint offset
    ) {
        for (final KnownObject knownObject : knownObjects) {
            final IntPoint worldObjectPosition = worldObject.getPosition();
            if (!Objects.equals(worldObjectPosition.add(offset), knownObject.getPosition())) {
                continue;
            }

            if (Objects.equals(worldObject.getResourceId(), knownObject.getResourceId())) {
                return knownObject;
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

    private Result<KnownObjectRepository.SpaceInfo> findReferencePoint(final List<WorldObject> worldObjects) {

        if (worldObjects.isEmpty()) {
            return Result.fail(ServerResultCode.KMS_WORLD_OBJECTS_LOW_COUNT);
        }

        final List<WorldObject> woExclusions = new ArrayList<>();

        for (int trying = 0; trying < 5; trying++) {

            final Result<KnownObjectRepository.SpaceInfo> result = findReferencePoint(worldObjects, woExclusions);
            if (result.isSuccess()) {
                return result;
            } else {
                log.info("Fail to find reference point, {}", result);
            }

        }

        return Result.fail(ServerResultCode.KMS_REFERENCE_POINT_TRYING_LIMIT_EXCEEDED);
    }

    private Result<KnownObjectRepository.SpaceInfo> findReferencePoint(final List<WorldObject> worldObjects, final List<WorldObject> woExclusion) {
        final List<Long> resourceExclusions = new ArrayList<>(RESTRICTED_OBJECT_RESOURCES);
        return selectReferenceObject(worldObjects, woExclusion, resourceExclusions)
                .thenApplyCombine(firstWo -> selectReferenceObject(worldObjects, woExclusion, resourceExclusions)
                                    .thenApplyCombine(secondWo -> findReferencePoint(firstWo, secondWo))
                );
    }

    private Result<WorldObject> selectReferenceObject(
            final List<WorldObject> worldObjects,
            final List<WorldObject> woExclusions,
            final List<Long> resourceExclusions
    ) {
        return worldObjects.stream()
                .filter(wo -> !woExclusions.contains(wo) && !resourceExclusions.contains(wo.getResourceId()))
                .findFirst()
                .map(wo -> {
                    woExclusions.add(wo);
                    resourceExclusions.add(wo.getResourceId());
                    return Result.ok(wo);
                })
                .orElse(Result.fail(ServerResultCode.KMS_REFERENCE_POINT_NO_SUITABLE_OBJECT));
    }

    private Result<KnownObjectRepository.SpaceInfo> findReferencePoint(final WorldObject firstObject, final WorldObject secondObject) {
        final List<KnownObjectRepository.SpaceInfo> spaceCandidates = knownObjectRepository.findSpaceByPattern(
                firstObject.getResourceId(),
                firstObject.getPosition().getX(),
                firstObject.getPosition().getY(),
                secondObject.getResourceId(),
                secondObject.getPosition().getX(),
                secondObject.getPosition().getY()
        );

        if (spaceCandidates.isEmpty()) {
            return Result.fail(ServerResultCode.KMS_NO_SPACE_CANDIDATES);
        }

        if (spaceCandidates.size() > 1) {
            return Result.fail(ServerResultCode.KMS_TOO_MANY_SPACE_CANDIDATES);
        }

        return Result.ok(spaceCandidates.get(0));
    }

}
