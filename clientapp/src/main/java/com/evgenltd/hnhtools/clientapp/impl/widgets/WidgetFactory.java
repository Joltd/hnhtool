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

    public static WidgetImpl build(final Integer id, final String type, final ArrayNode args) {
        switch (type) {
            case "inv":
                return new InventoryImpl(id, type, args);
            case "item":
                return new ItemImpl(id, type, args);
            case "isbox":
                return new BoxImpl(id, type, args);

            case "gameui":
                return new WidgetImpl(id, type, args);
            case "mapview":
                return new WidgetImpl(id, type, args);
            case "epry":
                return new WidgetImpl(id, type, args);
            case "chr":
                return new WidgetImpl(id, type, args);
            case "speedget":
                return new WidgetImpl(id, type, args);
            case "scm":
                return new WidgetImpl(id, type, args);
            case "sm":
                return new WidgetImpl(id, type, args);
            default:
                return new WidgetImpl(id, type, args);
        }
    }

}
