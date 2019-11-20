package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.clientapp.WorldObject;
import com.evgenltd.hnhtools.entity.IntPoint;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 19-11-2019 17:18
 */
final class WorldObjectImpl implements WorldObject {

    private Long id;
    private Integer frame;
    private IntPoint position;
    private boolean moving;
    private Long resourceId;

    WorldObjectImpl(final Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    Integer getFrame() {
        return frame;
    }
    void setFrame(final Integer frame) {
        this.frame = frame;
    }

    @Override
    public IntPoint getPosition() {
        return position;
    }
    void setPosition(final IntPoint position) {
        this.position = position;
    }

    @Override
    public boolean isMoving() {
        return moving;
    }
    void setMoving(final boolean moving) {
        this.moving = moving;
    }

    @Override
    public Long getResourceId() {
        return resourceId;
    }
    void setResourceId(final Long resourceId) {
        this.resourceId = resourceId;
    }
}
