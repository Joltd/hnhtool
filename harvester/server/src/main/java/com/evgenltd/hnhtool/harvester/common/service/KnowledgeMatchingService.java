package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester.common.component.InventoryIndex;
import com.evgenltd.hnhtool.harvester.common.component.ObjectIndex;
import com.evgenltd.hnhtool.harvester.common.entity.KnownItem;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.ServerResultCode;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.common.repository.KnownItemRepository;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.common.repository.SpaceRepository;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.entity.WorldInventory;
import com.evgenltd.hnhtools.complexclient.entity.WorldItem;
import com.evgenltd.hnhtools.complexclient.entity.WorldObject;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private KnownItemRepository knownItemRepository;

    public KnowledgeMatchingService(
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository,
            final KnownItemRepository knownItemRepository
    ) {
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
        this.knownItemRepository = knownItemRepository;
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

            if (Assert.isEmpty(worldObject.getResource())) {
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
                .filter(wo -> !Assert.isEmpty(wo.getResource()) && !ResourceConstants.isWaste(wo.getResource()))
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

            if (Objects.equals(worldObject.getResource(), knownObject.getResource())) {
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

        final String resource = worldObject.getResource();
        if (!Assert.isEmpty(resource)) {
            knownObject.setResource(resource);
        }

        knownObject.setX(offset.getX() + worldObject.getPosition().getX());
        knownObject.setY(offset.getY() + worldObject.getPosition().getY());
        knownObject.setActual(LocalDateTime.now());
        objectClassification(knownObject);
        return knownObjectRepository.save(knownObject);
    }

    private void objectClassification(final KnownObject knownObject) {
        final String resource = knownObject.getResource();
        if (ResourceConstants.isPlayer(resource)) {
            knownObject.setPlayer(true);
        } else if (ResourceConstants.isDoorway(resource)) {
            knownObject.setDoorway(true);
        } else if (ResourceConstants.isContainer(resource)) {
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
        final List<String> resourceExclusions = new ArrayList<>();
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
            final List<String> resourceExclusions
    ) {
        return worldObjects.stream()
                .filter(wo -> !woExclusions.contains(wo) && !resourceExclusions.contains(wo.getResource()))
                .findFirst()
                .map(wo -> {
                    woExclusions.add(wo);
                    resourceExclusions.add(wo.getResource());
                    return Result.ok(wo);
                })
                .orElse(Result.fail(ServerResultCode.KMS_REFERENCE_POINT_NO_SUITABLE_OBJECT));
    }

    private Result<KnownObject> findReferencePoint(final WorldObject firstObject, final WorldObject secondObject) {
        final List<KnownObject> spaceCandidates = knownObjectRepository.findSpaceByPattern(
                firstObject.getResource(),
                firstObject.getPosition().getX(),
                firstObject.getPosition().getY(),
                secondObject.getResource(),
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

    public void matchItems(final ObjectIndex objectIndex, final List<WorldInventory> inventories) {

        final InventoryIndex inventoryIndex = new InventoryIndex();

        List<WorldInventory> forNextIteration = inventories;

        while (true) {
            final List<WorldInventory> notHandled = matchItemsOneIteration(
                    objectIndex,
                    inventoryIndex,
                    forNextIteration
            );
            if (notHandled.isEmpty()) {
                return;
            }

            if (notHandled.size() == forNextIteration.size()) {
                log.warn("Some of inventories can not be matched with KDB");
                return;
            }

            forNextIteration = notHandled;
        }

    }

    private List<WorldInventory> matchItemsOneIteration(final ObjectIndex objectIndex, final InventoryIndex inventoryIndex, final List<WorldInventory> inventories) {

        final List<WorldInventory> notHandled = new ArrayList<>();

        for (final WorldInventory inventory : inventories) {

            final Result<ItemOwnerHandler> itemOwnerHandler = decideOwnerHandler(
                    inventory,
                    objectIndex,
                    inventoryIndex
            );
            if (itemOwnerHandler.isFailed()) {
                notHandled.add(inventory);
                continue;
            }

            final List<KnownItem> knownItems = itemOwnerHandler.getValue().loadKnownItems();

            for (final WorldItem worldItem : inventory.getItems()) {

                final ItemInfo itemInfo = readItemInfo(worldItem.getArguments());
                KnownItem knownItem = lookupKnownItem(worldItem.getResource(), itemInfo, knownItems);

                if (knownItem == null) {
                    knownItem = prepareItem(worldItem.getResource(), itemInfo);
                    itemOwnerHandler.getValue().setOwner(knownItem);
                }

                knownItem.setActual(LocalDateTime.now());
                knownItemRepository.save(knownItem);

                inventoryIndex.putMatch(knownItem.getId(), worldItem.getId());

            }

        }

        return notHandled;

    }

    private Result<ItemOwnerHandler> decideOwnerHandler(final WorldInventory inventory, final ObjectIndex objectIndex, final InventoryIndex inventoryIndex) {
        if (inventory.isObjectParentId()) {
            return objectIndex.getMatchedKnownObjectId(inventory.getObjectParentId())
                    .thenApply(knownObjectId -> new ItemOwnerHandler() {
                        @Override
                        public List<KnownItem> loadKnownItems() {
                            return knownItemRepository.findByOwnerId(knownObjectId);
                        }

                        @Override
                        public void setOwner(final KnownItem knownItem) {
                            knownObjectRepository.findById(knownObjectId).ifPresent(knownItem::setOwner);
                        }
                    });
        } else if (inventory.isItemParentId()) {
            return inventoryIndex.getMatchedKnownItemId(inventory.getItemParentId())
                    .thenApply(knownItemId -> new ItemOwnerHandler() {
                        @Override
                        public List<KnownItem> loadKnownItems() {
                            return knownItemRepository.findByParentId(knownItemId);
                        }

                        @Override
                        public void setOwner(final KnownItem knownItem) {
                            knownItemRepository.findById(knownItemId).ifPresent(knownItem::setParent);
                        }
                    });
        } else {
            // maybe return as Result?
            throw new ApplicationException("Unsupported parent type, type=[%s]", inventory.getParentId().getClass());
        }
    }

    private KnownItem lookupKnownItem(
            final String worldResource,
            final ItemInfo itemInfo,
            final List<KnownItem> knownItems
    ) {

        for (final KnownItem knownItem : knownItems) {
            final boolean resourceMatches = Objects.equals(knownItem.getResource(), worldResource);
            if (!resourceMatches) {
                continue;
            }

            if (Objects.equals(knownItem.getQuality(), itemInfo.getQuality())) {
                return knownItem;
            }
        }

        return null;
    }

    private ItemInfo readItemInfo(final List arguments) {
        return new ItemInfo();
    }

    private KnownItem prepareItem(
            final String resource,
            final ItemInfo itemInfo
    ) {
        final KnownItem knownItem = new KnownItem();
        knownItem.setResource(resource);
        knownItem.setQuality(itemInfo.getQuality());
        return knownItem;
    }

    private static class ItemInfo {
        private Double quality;

        public Double getQuality() {
            return quality;
        }

        public void setQuality(final Double quality) {
            this.quality = quality;
        }
    }

    private interface ItemOwnerHandler {
        List<KnownItem> loadKnownItems();

        void setOwner(final KnownItem knownItem);
    }

}
