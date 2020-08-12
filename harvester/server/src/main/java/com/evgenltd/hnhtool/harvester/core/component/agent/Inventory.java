package com.evgenltd.hnhtool.harvester.core.component.agent;

import com.evgenltd.hnhtools.clientapp.widgets.InventoryWidget;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.common.ApplicationException;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private Long knownObjectId;
    private InventoryWidget widget;
    private List<ItemWidget> items = new ArrayList<>();

    public boolean isOpened() {
        return widget != null;
    }

    public void clearWidget() {
        widget = null;
        items.clear();
    }

    public Long getKnownObjectId() {
        return knownObjectId;
    }
    public void setKnownObjectId(final Long knownObjectId) {
        this.knownObjectId = knownObjectId;
    }

    public InventoryWidget getWidget() {
        return widget;
    }
    public InventoryWidget getWidgetOrThrow() {
        if (!isOpened()) {
            throw new ApplicationException("There is no opened inventory");
        }
        return getWidget();
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
