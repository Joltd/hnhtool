package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.entity.WorldObject;
import com.evgenltd.hnhtools.complexclient.entity.impl.WorldObjectImpl;
import com.evgenltd.hnhtools.entity.ResultCode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 11:59</p>
 */
final class ObjectIndex {

    private final Map<Long, WorldObjectImpl> index = new HashMap<>();

    synchronized WorldObjectImpl getObject(final Long objectId) {
        return index.computeIfAbsent(objectId, WorldObjectImpl::new);
    }

    synchronized void removeObject(final Long objectId) {
        index.remove(objectId);
    }

    synchronized List<WorldObject> getObjectList() {
        return new ArrayList<>(index.values());
    }

    @Nullable
    synchronized WorldObjectImpl getWorldObject(final Long id) {
        return index.get(id);
    }

    synchronized Result<WorldObjectImpl> getWorldObjectIfPossible(final Long id) {
        final WorldObjectImpl worldObject = getWorldObject(id);
        return worldObject != null
                ? Result.ok(worldObject)
                : Result.fail(ResultCode.NO_WORLD_OBJECT);
    }

}
