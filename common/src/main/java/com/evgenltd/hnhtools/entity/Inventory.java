package com.evgenltd.hnhtools.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 20:22</p>
 */
public class Inventory {

    private Integer id;
    private IntPoint size;
    private List<WorldItem> items = new ArrayList<>();

    public Inventory(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public IntPoint getSize() {
        return size;
    }

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
