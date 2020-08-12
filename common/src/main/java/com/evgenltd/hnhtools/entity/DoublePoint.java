package com.evgenltd.hnhtools.entity;

import java.util.Objects;

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

    public DoublePoint add(final double x, final double y) {
        return new DoublePoint(this.x + x, this.y + y);
    }

    public DoublePoint add(final DoublePoint point) {
        return add(point.getX(), point.getY());
    }

    public DoublePoint sub(final double x, final double y) {
        return new DoublePoint(this.x - x, this.y - y);
    }

    public DoublePoint sub(final DoublePoint point) {
        return sub(point.getX(), point.getY());
    }

    public DoublePoint mul(final DoublePoint point) {
        return new DoublePoint(getX() * point.getX(), getY() * point.getY());
    }

    public DoublePoint div(final DoublePoint doublePoint) {
        return new DoublePoint(x / doublePoint.getX(), y / doublePoint.getY());
    }

    public DoublePoint div(final Number value) {
        return new DoublePoint(x / value.doubleValue(), y / value.doubleValue());
    }

    public IntPoint asIntPoint() {
        return new IntPoint((int) x, (int) y);
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
        final DoublePoint that = (DoublePoint) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
