package com.evgenltd.hnhtool.harvester.core.component.agent;

import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.common.ApplicationException;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-11-2019 23:47</p>
 */
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
        throw new ApplicationException("Hand hold another item");
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
}
