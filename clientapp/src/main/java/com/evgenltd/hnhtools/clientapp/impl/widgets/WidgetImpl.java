package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class WidgetImpl implements Widget {

    private final Integer id;
    private final String type;
    private final Integer parentId;
    private final ArrayNode args;

    WidgetImpl(final WidgetImpl widget) {
        this.id = widget.id;
        this.type = widget.type;
        this.parentId = widget.parentId;
        this.args = widget.args;
    }

    WidgetImpl(final Integer id, final String type, final Integer parentId, final ArrayNode args) {
        this.id = id;
        this.type = type;
        this.parentId = parentId;
        this.args = args;
    }

    public WidgetImpl copy() {
        return new WidgetImpl(this);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Integer getParentId() {
        return parentId;
    }

    @Override
    public ArrayNode getArgs() {
        return args;
    }

    public void handleMessage(final WidgetState.RelAccessor message) {}
}
