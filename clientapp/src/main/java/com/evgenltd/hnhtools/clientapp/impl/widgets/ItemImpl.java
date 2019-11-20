package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.widgets.Item;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 23:26</p>
 */
public final class ItemImpl extends WidgetImpl implements Item {

    public ItemImpl(final Integer id, final String type, final ArrayNode args) {
        super(id, type, args);
    }

}
