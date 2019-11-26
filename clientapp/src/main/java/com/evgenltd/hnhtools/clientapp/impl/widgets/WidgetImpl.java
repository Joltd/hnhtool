package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.fasterxml.jackson.databind.JsonNode;
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

    WidgetImpl(final Integer id, final String type, final ArrayNode args) {
        this.id = id;
        this.type = type;
        this.args = args;
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

    public void handleMessage(final JsonNode message) {}

}
