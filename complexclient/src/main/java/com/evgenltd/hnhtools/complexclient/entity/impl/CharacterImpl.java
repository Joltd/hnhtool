package com.evgenltd.hnhtools.complexclient.entity.impl;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 22:04</p>
 */
public class CharacterImpl {

    private Long id;
    private String name;
    private InventoryImpl equip;
    private InventoryImpl main;
    private InventoryImpl study;
    private Integer sheetId;
    private Integer speedId;

    public Long getId() {
        return id;
    }
    public synchronized void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public synchronized void setName(final String name) {
        this.name = name;
    }

    public InventoryImpl getEquip() {
        return equip;
    }
    public synchronized void setEquip(final InventoryImpl equip) {
        this.equip = equip;
    }

    public InventoryImpl getMain() {
        return main;
    }
    public synchronized void setMain(final InventoryImpl main) {
        this.main = main;
    }

    public InventoryImpl getStudy() {
        return study;
    }
    public synchronized void setStudy(final InventoryImpl study) {
        this.study = study;
    }

    public Integer getSheetId() {
        return sheetId;
    }
    public synchronized void setSheetId(final Integer sheetId) {
        this.sheetId = sheetId;
    }

    public Integer getSpeedId() {
        return speedId;
    }
    public synchronized void setSpeedId(final Integer speedId) {
        this.speedId = speedId;
    }

}
