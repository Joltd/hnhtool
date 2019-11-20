package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.messagebroker.RelType;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 19-11-2019 00:28</p>
 */
final class WidgetState {

    private final Map<Integer, WidgetImpl> index = new HashMap<>();

    private ResourceState resourceState;

    WidgetState(final ResourceState resourceState) {
        this.resourceState = resourceState;
    }

    void receiveRel(final JsonNode relNode) {
        final RelAccessor rel = new RelAccessor(relNode);
        final RelType type = rel.getRelType();
        if (type == null) {
            return;
        }

        final Integer widgetId = rel.getWidgetId();

        switch (type) {
            case REL_MESSAGE_NEW_WIDGET:
            case REL_MESSAGE_ADD_WIDGET:
                index.put(widgetId, null);
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:

                break;
            case REL_MESSAGE_DESTROY_WIDGET:
                index.remove(widgetId);
                break;
            case REL_MESSAGE_RESOURCE_ID:
                resourceState.putResource(relNode);
                break;
            case REL_MESSAGE_CHARACTER_ATTRIBUTE:
                break;
        }
    }

    private static final class RelAccessor {
        private static final String REL_TYPE = "relType";
        private static final String WIDGET_ID = "id";
        private static final String WIDGET_TYPE = "type";
        private JsonNode data;

        RelAccessor(final JsonNode data) {
            this.data = data;
        }

        RelType getRelType() {
            return JsonUtil.asCustomFromText(data, REL_TYPE, RelType::valueOf);
        }

        Integer getWidgetId() {
            return JsonUtil.asInt(data, WIDGET_ID);
        }

        String getWidgetType() {
            return JsonUtil.asText(data, WIDGET_TYPE);
        }
    }

}
