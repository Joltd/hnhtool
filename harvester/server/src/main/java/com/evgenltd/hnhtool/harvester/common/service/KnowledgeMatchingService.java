package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester.common.component.ObjectIndex;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Resource;
import com.evgenltd.hnhtool.harvester.common.entity.ServerResultCode;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.common.repository.ResourceRepository;
import com.evgenltd.hnhtool.harvester.common.repository.SpaceRepository;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.entity.WorldObject;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

    private static final List<Long> RESTRICTED_OBJECT_RESOURCES = Collections.singletonList(
            0L
    );

    private ResourceRepository resourceRepository;
    private SpaceRepository spaceRepository;
    private KnownObjectRepository knownObjectRepository;

    public KnowledgeMatchingService(
            final ResourceRepository resourceRepository,
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository
    ) {
        this.resourceRepository = resourceRepository;
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
    }

    // ##################################################
    // #                                                #
    // #  Object matching                               #
    // #                                                #
    // ##################################################

    @SuppressWarnings("WeakerAccess")
    public Result<ObjectIndex> matchObjects(
            @Nullable final ObjectIndex oldIndex,
            @NotNull final WorldObject woCharacter,
            @NotNull KnownObject koCharacter,
            @Nullable final List<WorldObject> objects,
            final boolean research
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

        final Result<KnownObject> referencePoint = findReferencePoint(objectsForMatching);
        if (referencePoint.isFailed() && !research) {
            return referencePoint.cast();
        }

        final KnownObject koReferencePoint = referencePoint.isSuccess()
                ? referencePoint.getValue()
                : buildNewSpaceDescriptor(woCharacter.getPosition());
        objectIndex.putOffset(
                koReferencePoint.getX(),
                koReferencePoint.getY()
        );

        koCharacter.setX(woCharacter.getPosition().getX() + koReferencePoint.getX());
        koCharacter.setY(woCharacter.getPosition().getY() + koReferencePoint.getY());
        koCharacter.setOwner(koReferencePoint.getOwner());
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

            if (Assert.isEmpty(worldObject.getResourceId())) {
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
                matchedKnownObject = rememberWorldObject(koCharacter.getOwner(), worldObject, objectIndex.getOffset());
            } else {
                matchedKnownObject.setActual(LocalDateTime.now());
                knownObjectRepository.save(matchedKnownObject);
            }

            objectIndex.putMatch(matchedKnownObject.getId(), worldObject.getId());

            knownObjects.remove(matchedKnownObject);

        }

        return Result.ok(objectIndex);
    }

    private List<WorldObject> filterWasteObjects(final List<WorldObject> objects) {
        return objects.stream()
                .filter(wo -> resourceRepository.findById(wo.getResourceId())
                        .map(resource -> !ResourceConstants.isWaste(resource.getName()))
                        .orElse(false))
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

            if (Objects.equals(worldObject.getResourceId(), knownObject.getResource().getId())) {
                return knownObject;
            }
        }

        return null;
    }

    @SuppressWarnings("WeakerAccess")
    public KnownObject rememberCharacterObject(final WorldObject worldObject) {
        return rememberWorldObject(null, worldObject, new IntPoint());
    }

    private KnownObject rememberWorldObject(final Space space, final WorldObject worldObject, final IntPoint offset) {
        final KnownObject knownObject = new KnownObject();
        knownObject.setOwner(space);

        if (worldObject.getResourceId() != null && worldObject.getResourceId() != 0) {
            final Resource resource = resourceRepository.findById(worldObject.getResourceId())
                    .orElseGet(() -> {
                        final Resource newResource = new Resource();
                        newResource.setId(worldObject.getResourceId());
                        return resourceRepository.save(newResource);
                    });
            knownObject.setResource(resource);
        }

        knownObject.setX(offset.getX() + worldObject.getPosition().getX());
        knownObject.setY(offset.getY() + worldObject.getPosition().getY());
        knownObject.setActual(LocalDateTime.now());
        objectClassification(knownObject);
        return knownObjectRepository.save(knownObject);
    }

    private void objectClassification(final KnownObject knownObject) {
        final Resource resource = knownObject.getResource();
        if (resource == null) {
            return;
        }

        final String resourceName = resource.getName();
        if (ResourceConstants.isPlayer(resourceName)) {
            knownObject.setPlayer(true);
        } else if (ResourceConstants.isDoorway(resourceName)) {
            knownObject.setDoorway(true);
        } else if (ResourceConstants.isContainer(resourceName)) {
            knownObject.setContainer(true);
        }
    }

    private Result<KnownObject> findReferencePoint(final List<WorldObject> worldObjects) {

        if (worldObjects.isEmpty()) {
            return Result.fail(ServerResultCode.KMS_WORLD_OBJECTS_LOW_COUNT);
        }

        final List<WorldObject> woExclusions = new ArrayList<>();

        for (int trying = 0; trying < 5; trying++) {

            final Result<KnownObject> result = findReferencePoint(worldObjects, woExclusions);
            if (result.isSuccess()) {
                return result;
            } else {
                log.info("Fail to find reference point, {}", result);
            }

        }

        return Result.fail(ServerResultCode.KMS_REFERENCE_POINT_TRYING_LIMIT_EXCEEDED);
    }

    private Result<KnownObject> findReferencePoint(final List<WorldObject> worldObjects, final List<WorldObject> woExclusion) {
        final List<Long> resourceExclusions = new ArrayList<>(RESTRICTED_OBJECT_RESOURCES);
        return selectReferenceObject(worldObjects, woExclusion, resourceExclusions)
                .thenApplyCombine(firstWo -> selectReferenceObject(worldObjects, woExclusion, resourceExclusions)
                                    .thenApplyCombine(secondWo -> findReferencePoint(firstWo, secondWo))
                        .then(ko -> {
                            ko.setX(ko.getX() - firstWo.getPosition().getX());
                            ko.setY(ko.getY() - firstWo.getPosition().getY());
                        })
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

    private Result<KnownObject> findReferencePoint(final WorldObject firstObject, final WorldObject secondObject) {
        final List<KnownObject> spaceCandidates = knownObjectRepository.findSpaceByPattern(
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

    private KnownObject buildNewSpaceDescriptor(final IntPoint offset) {
        final Space space = spaceRepository.save(new Space());

        final KnownObject knownObject = new KnownObject();
        knownObject.setX(-offset.getX());
        knownObject.setY(-offset.getY());
        knownObject.setOwner(space);
        return knownObject;
    }

    // ##################################################
    // #                                                #
    // #  Item matching                                 #
    // #                                                #
    // ##################################################

    public void matchItems() {



    }

}
