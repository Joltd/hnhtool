package com.evgenltd.hnhtools.environment;

import com.evgenltd.hnhtools.entity.DoublePoint;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-03-2019 23:12</p>
 */
public class WorldObject {

    private long id;
    private int frame;
    private DoublePoint position;
    private int resourceId;

    public WorldObject(final Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(final int frame) {
        this.frame = frame;
    }

    public DoublePoint getPosition() {
        return position;
    }

    public void setPosition(final DoublePoint position) {
        this.position = position;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(final int resourceId) {
        this.resourceId = resourceId;
    }
}
