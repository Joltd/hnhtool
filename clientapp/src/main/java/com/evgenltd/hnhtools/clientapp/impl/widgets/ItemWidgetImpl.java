package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 23:26</p>
 */
public final class ItemWidgetImpl extends WidgetImpl implements ItemWidget {

    private static final String LABEL_NAME = "tt";

    private IntPoint position;
    private Long resourceId;
    private String resource;
    private Map<String, List<JsonNode>> infoResources = new HashMap<>();

    private ItemWidgetImpl(final ItemWidgetImpl itemWidget) {
        super(itemWidget);
        this.position = itemWidget.position;
        this.resource = itemWidget.resource;
    }

    ItemWidgetImpl(
            final Integer id,
            final String type,
            final Integer parentId,
            final ArrayNode childArgs,
            final ArrayNode parentArgs
    ) {
        super(id, type, parentId, childArgs);
        position = JsonUtil.asPoint(parentArgs.get(0));
        resourceId = JsonUtil.asLong(childArgs.get(0));
    }

    @Override
    public WidgetImpl copy() {
        return new ItemWidgetImpl(this);
    }

    @Override
    public IntPoint getPosition() {
        return position;
    }

    public Long getResourceId() {
        return resourceId;
    }

    @Override
    public String getResource() {
        return resource;
    }
    public void setResource(final String resource) {
        this.resource = resource;
    }

    @Override
    public Map<String, List<JsonNode>> getInfoResources() {
        return infoResources;
    }

    @Override
    public void handleMessage(final WidgetState.RelAccessor message) {
        if (Objects.equals(message.getWidgetMessageName(), LABEL_NAME)) {
            infoResources.clear();

            for (final JsonNode infoNode : message.getArgs()) {
                if (!infoNode.isArray()) {
                    continue;
                }

                final List<JsonNode> args = StreamSupport.stream(infoNode.spliterator(), false)
                        .skip(1)
                        .collect(Collectors.toList());
                infoResources.put(JsonUtil.asText(infoNode.get(0)), args);
            }
        }
    }
}
