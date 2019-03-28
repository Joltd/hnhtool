package com.evgenltd.hnhtools.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 27-03-2019 00:16</p>
 */
public class DoublePoint {

    private double x;
    private double y;

    public DoublePoint(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public DoublePoint devide(final DoublePoint doublePoint) {
        return new DoublePoint(x / doublePoint.getX(), y / doublePoint.getY());
    }

    public IntPoint asIntPoint() {
        return new IntPoint((int) x, (int) y);
    }

}
