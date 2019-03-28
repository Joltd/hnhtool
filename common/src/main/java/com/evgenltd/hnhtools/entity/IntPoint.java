package com.evgenltd.hnhtools.entity;

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
}
