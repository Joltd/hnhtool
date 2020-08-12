package com.evgenltd.hnhtool.harvester.core.component.matcher;

public final class MatchingEntry<L, R> {
    private final L left;
    private final R right;

    MatchingEntry(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
