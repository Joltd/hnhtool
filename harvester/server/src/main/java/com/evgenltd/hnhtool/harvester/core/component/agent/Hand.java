package com.evgenltd.hnhtool.harvester.core.component.agent;

import com.evgenltd.hnhtool.harvester.core.component.exception.HandNotEmptyException;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.common.ApplicationException;

public class Hand {

    private Long knownItemId;
    private ItemWidget item;

    public boolean isEmpty() {
        return item == null;
    }
    public void checkEmpty() {
        if (isEmpty()) {
            return;
        }
        throw new HandNotEmptyException();
    }

    public Long getKnownItemId() {
        return knownItemId;
    }
    public void setKnownItemId(final Long knownItemId) {
        this.knownItemId = knownItemId;
    }

    public ItemWidget getItem() {
        return item;
    }
    public ItemWidget getItemOrThrow() {
        if (isEmpty()) {
            throw new ApplicationException("There is no in hand");
        }
        return getItem();
    }
    public void setItem(final ItemWidget item) {
        this.item = item;
    }

    public Record toRecord() {
        if (item != null) {
            return new Record(knownItemId, false, item.getResource());
        } else {
            return new Record(knownItemId, true, null);
        }
    }

    public static final record Record(Long knownItemId, boolean isEmpty, String resource) {}
}
