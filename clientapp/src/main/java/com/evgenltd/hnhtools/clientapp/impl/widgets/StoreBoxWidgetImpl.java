package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.widgets.StoreBoxWidget;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 21:40</p>
 */
public final class StoreBoxWidgetImpl extends WidgetImpl implements StoreBoxWidget {

    private Integer first;
    private Integer second;
    private Integer third;

    private StoreBoxWidgetImpl(final StoreBoxWidgetImpl storeBoxWidget) {
        super(storeBoxWidget);
        this.first = storeBoxWidget.first;
        this.second = storeBoxWidget.second;
        this.third = storeBoxWidget.third;
    }

    StoreBoxWidgetImpl(final Integer id, final String type, final ArrayNode args) {
        super(id, type, args);
    }

    @Override
    public StoreBoxWidgetImpl copy() {
        return new StoreBoxWidgetImpl(this);
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
