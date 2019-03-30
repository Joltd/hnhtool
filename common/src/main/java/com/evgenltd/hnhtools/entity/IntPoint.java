package com.evgenltd.hnhtools.entity;

import java.util.Objects;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 13-03-2019 00:02</p>
 */
public class IntPoint {

    private int x;
    private int y;

    public IntPoint() {
        this(0,0);
    }

    public IntPoint(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public IntPoint add(final int x, final int y) {
        return new IntPoint(this.x + x, this.y + y);
    }

    public DoublePoint multiple(final DoublePoint point) {
        return new DoublePoint(x * point.getX(), y * point.getY());
    }

    @Override
    public String toString() {
        return String.format("(%s;%s)", x, y);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IntPoint intPoint = (IntPoint) o;
        return x == intPoint.x &&
                y == intPoint.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
