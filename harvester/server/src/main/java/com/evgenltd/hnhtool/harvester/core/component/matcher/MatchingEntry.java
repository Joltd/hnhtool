package com.evgenltd.hnhtool.harvester.core.component.matcher;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-12-2019 22:52</p>
 */
public final class MatchingEntry<L, R> {
    private L left;
    private R right;

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
