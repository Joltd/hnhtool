package com.evgenltd.hnhtool.harvester.core.component.agent;

import com.evgenltd.hnhtools.clientapp.widgets.InventoryWidget;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 29-11-2019 02:50</p>
 */
public class Inventory {

    private InventoryWidget widget;
    private List<ItemWidget> items;

    public InventoryWidget getWidget() {
        return widget;
    }
    public void setWidget(final InventoryWidget widget) {
        this.widget = widget;
    }

    public List<ItemWidget> getItems() {
        return items;
    }
    public void setItems(final List<ItemWidget> items) {
        this.items = items;
    }
}
