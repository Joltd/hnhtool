package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.complexclient.entity.impl.WorldStackImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-04-2019 22:34</p>
 */
public class StackIndex {

    private final Map<Integer, WorldStackImpl> index = new HashMap<>();
    private final Map<Long, WorldStackImpl> parentIndex = new HashMap<>();

    synchronized void addStack(final WorldStackImpl worldStack) {
        index.put(worldStack.getId(), worldStack);
        parentIndex.put(worldStack.getParentObjectId(), worldStack);
    }

    synchronized void removeStack(final Integer id) {
        final WorldStackImpl worldStack = index.remove(id);
        if (worldStack != null) {
            parentIndex.remove(worldStack.getParentObjectId());
        }
    }

    @Nullable
    synchronized WorldStackImpl getStack(final Integer id) {
        return index.get(id);
    }

    @Nullable
    synchronized WorldStackImpl getStackByParentId(final Long parentId) {
        return parentIndex.get(parentId);
    }

}
