package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.clientapp.WorldObject;
import com.evgenltd.hnhtools.message.Message;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 19-11-2019 17:17
 */
final class WidgetImpl implements WorldObject {

    private static final String ID = "id";
    private static final String TYPE = "type";

    private Integer id;

    private JsonNode data;

    WidgetImpl(final Message.Rel rel) {
        this.data = rel.getData();
        this.id = getId(this.data);
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return JsonUtil.asText(data, TYPE);
    }

    public static Integer getId(final JsonNode node) {
        return JsonUtil.asInt(node, ID);
    }
}
