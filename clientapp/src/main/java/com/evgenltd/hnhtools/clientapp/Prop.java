package com.evgenltd.hnhtools.clientapp;

import com.evgenltd.hnhtools.entity.IntPoint;

public interface Prop {
    Long getId();

    IntPoint getPosition();

    boolean isMoving();

    String getResource();
}
