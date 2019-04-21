package com.evgenltd.hnhtools.complexclient.entity.impl;

import com.evgenltd.hnhtools.complexclient.entity.Inventory;
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
public class InventoryImpl implements Inventory {

    private Integer id;
    private Number parentId;
    private IntPoint size;
    private List<WorldItem> items = new ArrayList<>();

    public InventoryImpl(final Integer id) {
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
        items.add(worldItem);
    }

    public void removeItem(final WorldItem worldItem) {
        items.remove(worldItem);
    }

}
