package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;

public class WorldPoint {

    private Space space;
    private IntPoint position;

    public static WorldPoint of(final Space space, final IntPoint position) {
        final WorldPoint worldPoint = new WorldPoint();
        worldPoint.setSpace(space);
        worldPoint.setPosition(position);
        return worldPoint;
    }

    public Space getSpace() {
        return space;
    }
    public void setSpace(final Space space) {
        this.space = space;
    }

    public IntPoint getPosition() {
        return position;
    }
    public void setPosition(final IntPoint position) {
        this.position = position;
    }

}
