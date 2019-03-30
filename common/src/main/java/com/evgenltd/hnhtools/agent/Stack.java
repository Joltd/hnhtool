package com.evgenltd.hnhtools.agent;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 14:51</p>
 */
public final class Stack {

    private Integer id;
    private Integer count = 0;
    private Integer max = 0;

    public Stack(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCount() {
        return count;
    }
    public void setCount(final Integer count) {
        this.count = count;
    }

    public Integer getMax() {
        return max;
    }
    public void setMax(final Integer max) {
        this.max = max;
    }
}
