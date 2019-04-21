package com.evgenltd.hnhtool.harvester.common.component;

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
public class InventoryIndex {

    private final BiMap<Long, Integer> itemIndex = HashBiMap.create();

    public void putMatch(final Long knownItemId, final Integer worldItemId) {
        itemIndex.put(knownItemId, worldItemId);
    }

    public Result<Integer> getMatchedWorldItemId(final Long knownItemId) {
        return Result.ok(0);
    }

    public Result<Long> getMatchedKnownItemId(final Integer worldItemId) {
        return Result.ok(0L);
    }

}
