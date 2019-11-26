package com.evgenltd.hnhtool.harvester_old.common.component;

import com.evgenltd.hnhtool.harvester_old.common.entity.ServerResultCode;
import com.evgenltd.hnhtools.common.Result;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 17-04-2019 23:41</p>
 */
public class ItemIndex {

    private final BiMap<Long, Integer> itemIndex = HashBiMap.create();
    private final Map<Long, Integer> objectParentInventoryIndex = new HashMap<>();
    private final Map<Long, Integer> itemParentInventoryIndex = new HashMap<>();

    public void putItemMatch(final Long knownItemId, final Integer worldItemId) {
        itemIndex.put(knownItemId, worldItemId);
    }

    public void putInventoryMatchWithObjectParent(final Long parentKnownObjectId, final Integer worldInventoryId) {
        objectParentInventoryIndex.put(parentKnownObjectId, worldInventoryId);
    }

    public void putInventoryMatchWithItemParent(final Long parentKnownItemId, final Integer worldInventoryId) {
        itemParentInventoryIndex.put(parentKnownItemId, worldInventoryId);
    }

    public Result<Integer> getMatchedWorldItemId(final Long knownItemId) {
        final Integer worldItemId = itemIndex.get(knownItemId);
        if (worldItemId == null) {
            return Result.fail(ServerResultCode.NO_MATCHED_WORLD_ITEM_FOUND);
        }
        return Result.ok(worldItemId);
    }

    public Result<Long> getMatchedKnownItemId(final Integer worldItemId) {
        final Long knownItemId = itemIndex.inverse().get(worldItemId);
        if (knownItemId == null) {
            return Result.fail(ServerResultCode.NO_MATCHED_KNOWN_ITEM_FOUND);
        }
        return Result.ok(knownItemId);
    }

    public Result<Integer> getMatchedWorldInventoryId(final Long parentId) {
        Integer worldInventoryId = objectParentInventoryIndex.get(parentId);
        if (worldInventoryId == null) {
            worldInventoryId = itemParentInventoryIndex.get(parentId);
        }
        if (worldInventoryId == null) {
            return Result.fail(ServerResultCode.NO_MATCHED_WORLD_INVENTORY_FOUND);
        }
        return Result.ok(worldInventoryId);
    }

    public Result<Integer> getMatchedWorldInventoryIdByObjectParent(final Long parentKnownObjectId) {
        final Integer worldInventoryId = objectParentInventoryIndex.get(parentKnownObjectId);
        if (worldInventoryId == null) {
            return Result.fail(ServerResultCode.NO_MATCHED_WORLD_INVENTORY_FOUND);
        }
        return Result.ok(worldInventoryId);
    }

    public Result<Integer> getMatchedWorldInventoryIdByItemParent(final Long parentKnownItemId) {
        final Integer worldInventoryId = itemParentInventoryIndex.get(parentKnownItemId);
        if (worldInventoryId == null) {
            return Result.fail(ServerResultCode.NO_MATCHED_WORLD_INVENTORY_FOUND);
        }
        return Result.ok(worldInventoryId);
    }

}
