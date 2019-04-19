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
    private Integer sheetId;
    private Integer speedId;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public Inventory getEquip() {
        return equip;
    }
    public void setEquip(final Inventory equip) {
        this.equip = equip;
    }

    public Inventory getMain() {
        return main;
    }
    public void setMain(final Inventory main) {
        this.main = main;
    }

    public Inventory getStudy() {
        return study;
    }
    public void setStudy(final Inventory study) {
        this.study = study;
    }

    public Integer getSheetId() {
        return sheetId;
    }
    public void setSheetId(final Integer sheetId) {
        this.sheetId = sheetId;
    }

    public Integer getSpeedId() {
        return speedId;
    }
    public void setSpeedId(final Integer speedId) {
        this.speedId = speedId;
    }

}
