package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.widgets.InventoryWidget;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 20-11-2019 18:50
 */
final class InventoryWidgetImpl extends WidgetImpl implements InventoryWidget {

    private InventoryWidgetImpl(final InventoryWidgetImpl inventoryWidget) {
        super(inventoryWidget);
    }

    InventoryWidgetImpl(final Integer id, final String type, final ArrayNode args) {
        super(id, type, args);
    }

    @Override
    public InventoryWidgetImpl copy() {
        return new InventoryWidgetImpl(this);
    }
}
