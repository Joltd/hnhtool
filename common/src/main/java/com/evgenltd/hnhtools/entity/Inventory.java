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
    private List<Item> items = new ArrayList<>();

    public Inventory(final Integer id, final IntPoint size) {
        this.id = id;
        this.size = size;
    }

    public Integer getId() {
        return id;
    }

    public IntPoint getSize() {
        return size;
    }

    public List<Item> getItems() {
        return items;
    }

}
