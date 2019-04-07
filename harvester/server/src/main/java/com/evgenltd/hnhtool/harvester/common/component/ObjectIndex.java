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
 * <p>Created: 31-03-2019 13:11</p>
 */
public class ObjectIndex {

    private final BiMap<Long, Long> index = HashBiMap.create();

    public void putMatch(final Long knownObjectId, final Long worldObjectId) {
        index.put(knownObjectId, worldObjectId);
    }

    public Result<Long> getMatchedWorldObjectId(final Long knownObjectId) {
        final Long worldObjectId = index.get(knownObjectId);
        if (worldObjectId == null) {
            return Result.fail(ServerResultCode.NO_MATCHED_WORLD_OBJECT_FOUND);
        }

        return Result.ok(worldObjectId);
    }

    public Result<Long> getMatchedKnownObjectId(final Long worldObjectId) {
        final Long knownObjectId = index.inverse().get(worldObjectId);
        if (worldObjectId == null) {
            return Result.fail(ServerResultCode.NO_MATCHED_KNOWN_OBJECT_FOUND);
        }

        return Result.ok(knownObjectId);
    }

    @Override
    public String toString() {
        return String.format("%s", index.size());
    }
}
