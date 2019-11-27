package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 23:32</p>
 */
public class WidgetFactory {

    public static WidgetImpl build(
            final Integer id,
            final String type,
            final ArrayNode childArgs,
            final ArrayNode parentArgs
    ) {
        switch (type) {
            case "inv":
                return new InventoryWidgetImpl(id, type, childArgs);
            case "item":
                return new ItemWidgetImpl(id, type, childArgs, parentArgs);
            case "isbox":
                return new StoreBoxWidgetImpl(id, type, childArgs);

            case "gameui":
                return new WidgetImpl(id, type, childArgs);
            case "mapview":
                return new WidgetImpl(id, type, childArgs);
            case "epry":
                return new WidgetImpl(id, type, childArgs);
            case "chr":
                return new WidgetImpl(id, type, childArgs);
            case "speedget":
                return new WidgetImpl(id, type, childArgs);
            case "scm":
                return new WidgetImpl(id, type, childArgs);
            case "sm":
                return new WidgetImpl(id, type, childArgs);
            default:
                return new WidgetImpl(id, type, childArgs);
        }
    }

}
