package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.evgenltd.hnhtools.clientapp.widgets.InventoryWidget;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Objects;

final class InventoryWidgetImpl extends WidgetImpl implements InventoryWidget {

    private static final String SZ_NAME = "sz";

    private IntPoint size;

    private InventoryWidgetImpl(final InventoryWidgetImpl inventoryWidget) {
        super(inventoryWidget);
        this.size = inventoryWidget.size;
    }

    InventoryWidgetImpl(final Integer id, final String type, final Integer parentId, final ArrayNode args) {
        super(id, type, parentId, args);
        size = JsonUtil.asPoint(args.get(0));
    }

    @Override
    public InventoryWidgetImpl copy() {
        return new InventoryWidgetImpl(this);
    }

    @Override
    public IntPoint getSize() {
        return size;
    }

    @Override
    public void handleMessage(final WidgetState.RelAccessor message) {
        if (Objects.equals(message.getWidgetMessageName(), SZ_NAME)) {
            size = JsonUtil.asPoint(message.getArgs().get(0));
        }
    }
}
