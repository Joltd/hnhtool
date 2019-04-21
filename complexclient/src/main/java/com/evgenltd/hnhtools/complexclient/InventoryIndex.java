package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.complexclient.entity.WorldInventory;
import com.evgenltd.hnhtools.complexclient.entity.impl.WorldInventoryImpl;
import org.jetbrains.annotations.NotNull;
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
 * <p>Created: 21-04-2019 14:26</p>
 */
final class InventoryIndex {

    private final Map<Integer, WorldInventoryImpl> index = new HashMap<>();
    private final Map<Number, WorldInventoryImpl> parentIndex = new HashMap<>();

    synchronized void addInventory(final WorldInventoryImpl inventory) {
        index.put(inventory.getId(), inventory);
        parentIndex.put(inventory.getParentId(), inventory);
    }

    @Nullable
    synchronized WorldInventoryImpl getInventory(final Integer id) {
        return index.get(id);
    }

    @Nullable
    synchronized WorldInventoryImpl getInventoryByParentId(final Number parentId) {
        return parentIndex.get(parentId);
    }

    synchronized void removeInventory(final Integer id) {
        final WorldInventoryImpl inventory = index.remove(id);
        if (inventory != null) {
            parentIndex.get(inventory.getParentId());
        }
    }

    @NotNull
    synchronized List<WorldInventory> getInventories() {
        return new ArrayList<>(index.values());
    }

}
