package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.widgets.Box;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 21:40</p>
 */
public final class BoxImpl extends WidgetImpl implements Box {

    private Integer first;
    private Integer second;
    private Integer third;

    public BoxImpl(final Integer id, final String type, final ArrayNode args) {
        super(id, type, args);
    }

    @Override
    public Integer getFirst() {
        return first;
    }
    public void setFirst(final Integer first) {
        this.first = first;
    }

    @Override
    public Integer getSecond() {
        return second;
    }
    public void setSecond(final Integer second) {
        this.second = second;
    }

    @Override
    public Integer getThird() {
        return third;
    }
    public void setThird(final Integer third) {
        this.third = third;
    }

}
