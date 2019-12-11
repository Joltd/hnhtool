package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 23:32</p>
 */
public final class WidgetFactory {

    public static WidgetImpl build(final WidgetState.RelAccessor rel) {
        final Integer id = rel.getWidgetId();
        final String type = rel.getWidgetType();
        final Integer parentId = rel.getWidgetParentId();
        final ArrayNode childArgs = rel.getChildArgs();
        final ArrayNode parentArgs = rel.getParentArgs();

        switch (type) {
            case "inv":
                return new InventoryWidgetImpl(id, type, parentId, childArgs);
            case "item":
                return new ItemWidgetImpl(id, type, parentId, childArgs, parentArgs);
            case "isbox":
                return new StoreBoxWidgetImpl(id, type, parentId, childArgs);

            case "gameui":
                return new WidgetImpl(id, type, parentId, childArgs);
            case "mapview":
                return new WidgetImpl(id, type, parentId, childArgs);
            case "epry":
                return new WidgetImpl(id, type, parentId, childArgs);
            case "chr":
                return new WidgetImpl(id, type, parentId, childArgs);
            case "speedget":
                return new WidgetImpl(id, type, parentId, childArgs);
            case "scm":
                return new WidgetImpl(id, type, parentId, childArgs);
            case "sm":
                return new WidgetImpl(id, type, parentId, childArgs);
            default:
                return new WidgetImpl(id, type, parentId, childArgs);
        }
    }

}
