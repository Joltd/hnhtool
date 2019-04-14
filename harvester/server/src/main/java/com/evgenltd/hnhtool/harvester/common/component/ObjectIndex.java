package com.evgenltd.hnhtool.harvester.common.component;

import com.evgenltd.hnhtool.harvester.common.entity.ServerResultCode;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
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
    private IntPoint offset = new IntPoint();

    public void putMatch(final Long knownObjectId, final Long worldObjectId) {
        index.put(knownObjectId, worldObjectId);
    }

    public void putOffset(final Integer x, final Integer y) {
        offset = new IntPoint(x, y);
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

    public IntPoint getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return String.format("%s", index.size());
    }
}
