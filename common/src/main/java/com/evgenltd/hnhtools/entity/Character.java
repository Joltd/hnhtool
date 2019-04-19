package com.evgenltd.hnhtools.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 22:04</p>
 */
public class Character {

    private Long id;
    private String name;
    private Inventory equip;
    private Inventory main;
    private Inventory study;

    public void setName(final String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

}
