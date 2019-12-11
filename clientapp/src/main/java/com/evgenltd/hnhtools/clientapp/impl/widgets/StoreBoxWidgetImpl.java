package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.evgenltd.hnhtools.clientapp.widgets.StoreBoxWidget;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Objects;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 21:40</p>
 */
public final class StoreBoxWidgetImpl extends WidgetImpl implements StoreBoxWidget {

    private static final String CHANGE_NUMBER_NAME = "chnum";

    private Integer first;
    private Integer second;
    private Integer third;

    private StoreBoxWidgetImpl(final StoreBoxWidgetImpl storeBoxWidget) {
        super(storeBoxWidget);
        this.first = storeBoxWidget.first;
        this.second = storeBoxWidget.second;
        this.third = storeBoxWidget.third;
    }

    StoreBoxWidgetImpl(final Integer id, final String type, final Integer parentId, final ArrayNode args) {
        super(id, type, parentId, args);
        this.first = JsonUtil.asInt(args.get(1));
        this.second = JsonUtil.asInt(args.get(2));
        this.third = JsonUtil.asInt(args.get(3));
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

    @Override
    public void handleMessage(final WidgetState.RelAccessor message) {
        if (Objects.equals(message.getWidgetMessageName(), CHANGE_NUMBER_NAME)) {
            final ArrayNode args = message.getArgs();
            this.first = JsonUtil.asInt(args.get(0));
            this.second = JsonUtil.asInt(args.get(1));
            this.third = JsonUtil.asInt(args.get(2));
        }
    }

}
