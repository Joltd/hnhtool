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

        return switch (type) {
            case "inv" -> new InventoryWidgetImpl(id, type, parentId, childArgs);
            case "item" -> new ItemWidgetImpl(id, type, parentId, childArgs, parentArgs);
            case "isbox" -> new StoreBoxWidgetImpl(id, type, parentId, childArgs);
            case "charlist" -> new CharListWidgetImpl(id, type, parentId, childArgs);
//            case "gameui" -> new WidgetImpl(id, type, parentId, childArgs);
//            case "mapview" -> new WidgetImpl(id, type, parentId, childArgs);
//            case "epry" -> new WidgetImpl(id, type, parentId, childArgs);
//            case "chr" -> new WidgetImpl(id, type, parentId, childArgs);
//            case "speedget" -> new WidgetImpl(id, type, parentId, childArgs);
//            case "scm" -> new WidgetImpl(id, type, parentId, childArgs);
//            case "sm" -> new WidgetImpl(id, type, parentId, childArgs);
            default -> new WidgetImpl(id, type, parentId, childArgs);
        };
    }

}
