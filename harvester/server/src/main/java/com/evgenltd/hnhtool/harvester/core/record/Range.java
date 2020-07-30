package com.evgenltd.hnhtool.harvester.core.record;

import com.evgenltd.hnhtools.entity.IntPoint;

public record Range(int fromX, int fromY, int toX, int toY) {
    public IntPoint from() {
        return new IntPoint(fromX(), fromY());
    }

    public IntPoint to() {
        return new IntPoint(toX(), toY());
    }
}
