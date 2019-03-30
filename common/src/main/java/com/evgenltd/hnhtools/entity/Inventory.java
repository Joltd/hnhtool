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
    private List<Item> items = new ArrayList<>();

    public Inventory(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }

}
