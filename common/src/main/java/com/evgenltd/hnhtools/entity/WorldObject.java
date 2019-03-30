package com.evgenltd.hnhtools.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 20:58</p>
 */
public class WorldObject {

    private Long id;
    private IntPoint position;
    private Integer resourceId;
    private String resourceName;

    public WorldObject(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public IntPoint getPosition() {
        return position;
    }

    public void setPosition(final IntPoint position) {
        this.position = position;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(final Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }
}
