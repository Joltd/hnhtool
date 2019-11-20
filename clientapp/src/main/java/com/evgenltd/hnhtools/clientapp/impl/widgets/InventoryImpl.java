package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.widgets.Inventory;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 20-11-2019 18:50
 */
public final class InventoryImpl extends WidgetImpl implements Inventory {

    public InventoryImpl(final Integer id, final String type, final ArrayNode args) {
        super(id, type, args);
    }

}
