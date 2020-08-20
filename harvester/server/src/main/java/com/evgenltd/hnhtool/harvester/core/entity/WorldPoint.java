package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;

public class WorldPoint {

    private Long spaceId;
    private IntPoint position;

    public static WorldPoint of(final Long space, final IntPoint position) {
        final WorldPoint worldPoint = new WorldPoint();
        worldPoint.setSpaceId(space);
        worldPoint.setPosition(position);
        return worldPoint;
    }

    public Long getSpaceId() {
        return spaceId;
    }
    public void setSpaceId(final Long spaceId) {
        this.spaceId = spaceId;
    }

    public IntPoint getPosition() {
        return position;
    }
    public void setPosition(final IntPoint position) {
        this.position = position;
    }

}
