package com.evgenltd.hnhtools.complexclient.entity;

import com.evgenltd.hnhtools.entity.IntPoint;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 21-04-2019 14:37</p>
 */
public interface WorldObject {

    Long getId();

    IntPoint getPosition();

    Long getResourceId();

    boolean isMoving();

}
