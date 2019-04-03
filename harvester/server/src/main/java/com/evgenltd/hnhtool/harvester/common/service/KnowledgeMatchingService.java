package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester.common.component.ObjectIndex;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Resource;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.entity.WorldObject;
import org.jetbrains.annotations.NotNull;
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

    private KnownObjectRepository knownObjectRepository;
    private ResourceProviderImpl resourceProvider;

    public KnowledgeMatchingService(
            final KnownObjectRepository knownObjectRepository,
            final ResourceProviderImpl resourceProvider
    ) {
        this.knownObjectRepository = knownObjectRepository;
        this.resourceProvider = resourceProvider;
    }

    public ObjectIndex match(@NotNull final Space space, @NotNull final IntPoint characterPosition, final List<WorldObject> objects) {

        final ObjectIndex objectIndex = new ObjectIndex();
        if (objects == null || objects.isEmpty()) {
            return objectIndex;
        }

        Assert.valueRequireNonEmpty(space, "Space");
        Assert.valueRequireNonEmpty(characterPosition, "CharacterPosition");

        final List<WorldObject> objectsForMatching = filterWasteObjects(objects);

        final IntPoint upperLeft = characterPosition.add(-10000, -10000);
        final IntPoint lowerRight = characterPosition.add(10000, 10000);

        final List<KnownObject> knownObjects = knownObjectRepository.findObjectsInArea(
                upperLeft.getX(),
                upperLeft.getY(),
                lowerRight.getX(),
                lowerRight.getY()
        );

        for (final KnownObject knownObject : knownObjects) {

            final WorldObject matchedWorldObject = lookupMatchedObject(objectsForMatching, knownObject);
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
            final KnownObject knownObject = rememberWorldObject(space, worldObject);
            objectIndex.putMatch(knownObject.getId(), worldObject.getId());
        }

        return objectIndex;
    }

    private List<WorldObject> filterWasteObjects(final List<WorldObject> objects) {
        return objects.stream()
                .filter(wo -> !ResourceConstants.isWaste(wo.getResourceId()))
                .collect(Collectors.toList());
    }

    private WorldObject lookupMatchedObject(final List<WorldObject> worldObjects, final KnownObject knownObject) {
        for (final WorldObject worldObject : worldObjects) {
            if (!Objects.equals(worldObject.getPosition(), knownObject.getPosition())) {
                continue;
            }

            if (compareResource(knownObject.getResource(), worldObject.getResourceId())) {
                return worldObject;
            }
        }

        return null;
    }

    private boolean compareResource(final Resource resource, final Integer resourceId) {
        if (resource == null && resourceId == null) {
            return true;
        }

        if (resource != null && resourceId == null) {
            return false;
        }

        if (resource == null) {
            return false;
        }

        return Objects.equals(resource.getId(), resourceId.longValue());
    }

    private KnownObject rememberWorldObject(final Space space, final WorldObject worldObject) {
        final KnownObject knownObject = new KnownObject();
        knownObject.setOwner(space);
        knownObject.setResource(resourceProvider.findResource(worldObject.getResourceId()));
        knownObject.setX(worldObject.getPosition().getX());
        knownObject.setY(worldObject.getPosition().getY());
        knownObject.setActual(LocalDateTime.now());
        objectClassification(knownObject, space);
        return knownObjectRepository.save(knownObject);
    }

    private void objectClassification(
            final KnownObject knownObject,
            final Space space
    ) {
        final Long resourceId = knownObject.getResource().getId();
        if (ResourceConstants.isDoorway(resourceId)) {
            knownObject.setDoorway(true);
            knownObject.setSpaceFrom(space);
        } else if (ResourceConstants.isContainer(resourceId)) {
            knownObject.setContainer(true);
        }
    }

}
