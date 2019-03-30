package com.evgenltd.hnhtools.agent;

import com.evgenltd.hnhtools.entity.IntPoint;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-03-2019 23:12</p>
 */
final class WorldObject {

    private long id;
    private int frame;
    private IntPoint position;
    private int resourceId;
    private boolean moving;

    WorldObject(final Long id) {
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

    public IntPoint getPosition() {
        return position;
    }
    public void setPosition(final IntPoint position) {
        this.position = position;
    }

    public int getResourceId() {
        return resourceId;
    }
    public void setResourceId(final int resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isMoving() {
        return moving;
    }
    public void setMoving(final boolean moving) {
        this.moving = moving;
    }

}
