package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 19-11-2019 17:17
 */
public class WidgetImpl implements Widget {

    private Integer id;
    private String type;
    private Integer parentId;
    private ArrayNode args;

    WidgetImpl(final WidgetImpl widget) {
        this.id = widget.id;
        this.type = widget.type;
        this.parentId = widget.parentId;
        this.args = widget.args;
    }

    WidgetImpl(final Integer id, final String type, final ArrayNode args) {
        this.id = id;
        this.type = type;
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

    public void setParentId(final Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public Integer getParentId() {
        return parentId;
    }

    public void handleMessage(final WidgetState.RelAccessor message) {}
}
