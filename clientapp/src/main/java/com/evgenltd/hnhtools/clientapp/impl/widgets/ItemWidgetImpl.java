package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 23:26</p>
 */
final class ItemWidgetImpl extends WidgetImpl implements ItemWidget {

    private IntPoint position;

    private ItemWidgetImpl(final ItemWidgetImpl itemWidget) {
        super(itemWidget);
    }

    ItemWidgetImpl(
            final Integer id,
            final String type,
            final ArrayNode childArgs,
            final ArrayNode parentArgs
    ) {
        super(id, type, childArgs);
        final JsonNode positionNode = parentArgs.get(0);
        position = new IntPoint(
                JsonUtil.asInt(positionNode, "x"),
                JsonUtil.asInt(positionNode, "y")
        );
    }

    @Override
    public WidgetImpl copy() {
        return new ItemWidgetImpl(this);
    }

    @Override
    public IntPoint getPosition() {
        return position;
    }

}
