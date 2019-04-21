package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.complexclient.entity.impl.InventoryImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 21-04-2019 14:26</p>
 */
final class InventoryIndex {

    private final Map<Integer, InventoryImpl> index = new HashMap<>();
    private final Map<Number, InventoryImpl> parentIndex = new HashMap<>();

    synchronized void addInventory(final InventoryImpl inventory) {
        index.put(inventory.getId(), inventory);
        parentIndex.put(inventory.getParentId(), inventory);
    }

    @Nullable
    synchronized InventoryImpl getInventory(final Integer id) {
        return index.get(id);
    }

    @Nullable
    synchronized InventoryImpl getInventoryByParentId(final Number parentId) {
        return parentIndex.get(parentId);
    }

    synchronized void removeInventory(final Integer id) {
        final InventoryImpl inventory = index.remove(id);
        if (inventory != null) {
            parentIndex.get(inventory.getParentId());
        }
    }

}
