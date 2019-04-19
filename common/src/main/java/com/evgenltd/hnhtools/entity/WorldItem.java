package com.evgenltd.hnhtools.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 21:01</p>
 */
public class WorldItem {

    private Integer id;
    private Long resourceId;

    private IntPoint position;
    private Integer number; // for equip

    public WorldItem(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Long getResourceId() {
        return resourceId;
    }
    public void setResourceId(final Long resourceId) {
        this.resourceId = resourceId;
    }

    public IntPoint getPosition() {
        return position;
    }
    public void setPosition(final IntPoint position) {
        this.position = position;
    }

    public Integer getNumber() {
        return number;
    }
    public void setNumber(final Integer number) {
        this.number = number;
    }
}
