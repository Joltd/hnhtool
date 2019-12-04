package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 05-12-2019 01:47</p>
 */
public class WorldPoint {

    private Space space;
    private IntPoint position;

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
