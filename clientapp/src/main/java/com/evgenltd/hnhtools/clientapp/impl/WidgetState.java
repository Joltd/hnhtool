package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.clientapp.impl.widgets.ItemWidgetImpl;
import com.evgenltd.hnhtools.clientapp.impl.widgets.WidgetFactory;
import com.evgenltd.hnhtools.clientapp.impl.widgets.WidgetImpl;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.evgenltd.hnhtools.messagebroker.RelType;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 19-11-2019 00:28</p>
 */
public final class WidgetState {

    private final Map<Integer, WidgetImpl> index = new HashMap<>();

    private ResourceState resourceState;

    WidgetState(final ResourceState resourceState) {
        this.resourceState = resourceState;
    }

    synchronized void receiveRel(final JsonNode relNode) {
        final RelAccessor rel = new RelAccessor(relNode);
        final RelType type = rel.getRelType();
        if (type == null) {
            return;
        }

        final Integer widgetId = rel.getWidgetId();

        switch (type) {
            case REL_MESSAGE_NEW_WIDGET:
                final WidgetImpl newWidget = WidgetFactory.build(rel);
                index.put(widgetId, newWidget);
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:
                final WidgetImpl existedWidget = index.get(widgetId);
                if (existedWidget == null) {
                    break;
                }
                existedWidget.handleMessage(rel);
                break;
            case REL_MESSAGE_DESTROY_WIDGET:
                index.remove(widgetId);
                break;
            case REL_MESSAGE_ADD_WIDGET:
                break;
            case REL_MESSAGE_RESOURCE_ID:
                resourceState.putResource(relNode);
                break;
            case REL_MESSAGE_CHARACTER_ATTRIBUTE:
                break;
        }
    }

    synchronized boolean hasWidgets() {
        return !index.isEmpty();
    }

    synchronized boolean hasWidget(final Integer id) {
        return index.containsKey(id);
    }

    synchronized List<Widget> getWidgets() {
        return index.values()
                .stream()
                .peek(this::fillResource)
                .map(WidgetImpl::copy)
                .collect(Collectors.toList());
    }

    private void fillResource(final WidgetImpl widget) {
        if (widget instanceof ItemWidgetImpl) {
            final ItemWidgetImpl item = (ItemWidgetImpl) widget;
            final Long resourceId = item.getResourceId();
            if (resourceId == null) {
                return;
            }
            item.setResource(resourceState.getResource(resourceId));
        }
    }

    public static final class RelAccessor {
        private static final String REL_TYPE = "relType";
        private static final String WIDGET_ID = "id";
        private static final String WIDGET_TYPE = "type";
        private static final String WIDGET_PARENT_ID = "parent";
        private static final String WIDGET_MESSAGE_NAME = "name";
        private static final String CHILD_ARGS = "cArgs";
        private static final String PARENT_ARGS = "pArgs";
        private static final String ARGS = "args";
        private JsonNode data;

        RelAccessor(final JsonNode data) {
            this.data = data;
        }

        RelType getRelType() {
            return JsonUtil.asCustomFromText(data, REL_TYPE, RelType::valueOf);
        }

        public Integer getWidgetId() {
            return JsonUtil.asInt(data, WIDGET_ID);
        }

        public String getWidgetType() {
            return JsonUtil.asText(data, WIDGET_TYPE);
        }

        public Integer getWidgetParentId() {
            return JsonUtil.asInt(data, WIDGET_PARENT_ID);
        }

        public String getWidgetMessageName() {
            return JsonUtil.asText(data, WIDGET_MESSAGE_NAME);
        }

        public ArrayNode getChildArgs() {
            return JsonUtil.asArrayNode(data, CHILD_ARGS);
        }

        public ArrayNode getParentArgs() {
            return JsonUtil.asArrayNode(data, PARENT_ARGS);
        }

        public ArrayNode getArgs() {
            return JsonUtil.asArrayNode(data, ARGS);
        }
    }

}
