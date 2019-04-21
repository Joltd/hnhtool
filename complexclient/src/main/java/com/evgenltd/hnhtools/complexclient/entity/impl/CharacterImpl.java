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
    private WorldInventoryImpl equip;
    private WorldInventoryImpl main;
    private WorldInventoryImpl study;
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

    public WorldInventoryImpl getEquip() {
        return equip;
    }
    public synchronized void setEquip(final WorldInventoryImpl equip) {
        this.equip = equip;
    }

    public WorldInventoryImpl getMain() {
        return main;
    }
    public synchronized void setMain(final WorldInventoryImpl main) {
        this.main = main;
    }

    public WorldInventoryImpl getStudy() {
        return study;
    }
    public synchronized void setStudy(final WorldInventoryImpl study) {
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
