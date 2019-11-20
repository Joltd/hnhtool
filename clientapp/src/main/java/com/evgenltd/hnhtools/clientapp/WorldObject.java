package com.evgenltd.hnhtools.clientapp;

import com.evgenltd.hnhtools.entity.IntPoint;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 19-11-2019 00:17</p>
 */
public interface WorldObject {
    Long getId();

    IntPoint getPosition();

    boolean isMoving();

    Long getResourceId();
}
