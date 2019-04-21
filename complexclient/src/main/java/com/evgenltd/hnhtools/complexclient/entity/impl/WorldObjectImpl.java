package com.evgenltd.hnhtools.complexclient.entity.impl;

import com.evgenltd.hnhtools.complexclient.entity.WorldObject;
import com.evgenltd.hnhtools.entity.IntPoint;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 21-04-2019 14:46</p>
 */
public final class WorldObjectImpl implements WorldObject {

    private Long id;
    private int frame;
    private IntPoint position;
    private Long resourceId;
    private boolean moving;

    public WorldObjectImpl(final Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getFrame() {
        return frame;
    }
    public void setFrame(final int frame) {
        this.frame = frame;
    }

    @Override
    public IntPoint getPosition() {
        return position;
    }
    public void setPosition(final IntPoint position) {
        this.position = position;
    }

    @Override
    public Long getResourceId() {
        return resourceId;
    }
    public void setResourceId(final Long resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public boolean isMoving() {
        return moving;
    }
    public void setMoving(final boolean moving) {
        this.moving = moving;
    }

}
