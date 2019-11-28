package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Objects;

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
    private ArrayNode label;

    private ItemWidgetImpl(final ItemWidgetImpl itemWidget) {
        super(itemWidget);
        this.position = itemWidget.position;
        this.resource = itemWidget.resource;
    }

    ItemWidgetImpl(
            final Integer id,
            final String type,
            final ArrayNode childArgs,
            final ArrayNode parentArgs
    ) {
        super(id, type, childArgs);
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
    public ArrayNode getLabel() {
        return label;
    }

    @Override
    public void handleMessage(final WidgetState.RelAccessor message) {
        if (Objects.equals(message.getWidgetMessageName(), LABEL_NAME)) {
            label = message.getArgs();
        }
    }
}
