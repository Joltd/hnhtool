package com.evgenltd.hnhtool.harvester.research.entity;

import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtools.entity.IntPoint;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 05-04-2019 01:05</p>
 */
public class Location {

    private Space space;
    private IntPoint point;

    public Space getSpace() {
        return space;
    }
    public void setSpace(final Space space) {
        this.space = space;
    }

    public IntPoint getPoint() {
        return point;
    }
    public void setPoint(final IntPoint point) {
        this.point = point;
    }

}
