package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.entity.IntPoint;

final class PropImpl implements Prop {

    private Long id;
    private Integer frame = 0;
    private IntPoint position;
    private boolean moving;
    private Long resourceId;
    private String resource;

    PropImpl(final Long id) {
        this.id = id;
    }

    PropImpl copy() {
        final PropImpl prop = new PropImpl(id);
        prop.position = position;
        prop.moving = moving;
        prop.resourceId = resourceId;
        return prop;
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

    Long getResourceId() {
        return resourceId;
    }
    void setResourceId(final Long resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String getResource() {
        return resource;
    }
    void setResource(final String resource) {
        this.resource = resource;
    }

}
