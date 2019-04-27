package com.evgenltd.hnhtool.harvester.common.component;

import com.evgenltd.hnhtool.harvester.common.entity.ServerResultCode;
import com.evgenltd.hnhtools.common.Result;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 17-04-2019 23:41</p>
 */
public class ItemIndex {

    private final BiMap<Long, Integer> itemIndex = HashBiMap.create();

    public void putMatch(final Long knownItemId, final Integer worldItemId) {
        itemIndex.put(knownItemId, worldItemId);
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

}
