package com.evgenltd.hnhtools.entity;

import com.evgenltd.hnhtools.common.Assert;

import java.util.Objects;

public final class IntPoint {

    private final int x;
    private final int y;

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

    public DoublePoint asDouble() {
        return new DoublePoint(getX(), getY());
    }

    public IntPoint add(final IntPoint point) {
        return add(point.getX(), point.getY());
    }

    public IntPoint add(final int x, final int y) {
        return new IntPoint(this.x + x, this.y + y);
    }

    public IntPoint sub(final IntPoint point) {
        return add(-point.x, -point.y);
    }

    public DoublePoint mul(final DoublePoint point) {
        return new DoublePoint(getX() * point.getX(), getY() * point.getY());
    }

    public DoublePoint div(final DoublePoint modifier) {
        return new DoublePoint(getX() / modifier.getX(), getY() / modifier.getY());
    }

    public DoublePoint div(final Number value) {
        return asDouble().div(value);
    }

    public String asString() {
        return String.format("%s;%s", getX(), getY());
    }

    public static IntPoint valueOf(final String string) {
        if (Assert.isEmpty(string)) {
            return new IntPoint();
        }

        final String[] parts = string.split(";");
        if (parts.length != 2) {
            return new IntPoint();
        }

        try {
            return new IntPoint(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1])
            );
        } catch (NumberFormatException e) {
            return new IntPoint();
        }
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
