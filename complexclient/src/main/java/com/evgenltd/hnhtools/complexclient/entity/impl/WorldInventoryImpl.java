package com.evgenltd.hnhtools.complexclient.entity.impl;

import com.evgenltd.hnhtools.complexclient.entity.WorldInventory;
import com.evgenltd.hnhtools.complexclient.entity.WorldItem;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 20:22</p>
 */
public class WorldInventoryImpl implements WorldInventory {

    private Integer id;
    /**
     * WorldObject or WorldItem who is an actual owner of inventory
     */
    private Number parentId;
    private Integer windowId;
    private IntPoint size;
    private final List<WorldItem> items = new ArrayList<>();

    public WorldInventoryImpl(final Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setParentId(final Number parentId) {
        this.parentId = parentId;
    }
    @Override
    public Number getParentId() {
        return parentId;
    }

    public Integer getWindowId() {
        return windowId;
    }
    public void setWindowId(final Integer windowId) {
        this.windowId = windowId;
    }

    public void setSize(final IntPoint size) {
        this.size = size;
    }
    @Override
    public IntPoint getSize() {
        return size;
    }

    @Override
    public List<WorldItem> getItems() {
        return items;
    }

    public void addItem(final WorldItem worldItem) {
        synchronized (items) {
            items.add(worldItem);
        }
    }

    public void removeItem(final WorldItem worldItem) {
        synchronized (items) {
            items.remove(worldItem);
        }
    }

}
