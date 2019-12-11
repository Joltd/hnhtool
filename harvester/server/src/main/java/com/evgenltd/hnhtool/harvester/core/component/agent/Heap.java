package com.evgenltd.hnhtool.harvester.core.component.agent;

import com.evgenltd.hnhtools.clientapp.widgets.StoreBoxWidget;
import com.evgenltd.hnhtools.common.ApplicationException;

import java.util.Objects;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 29-11-2019 02:51</p>
 */
public class Heap {

    private Long knownObjectId;
    private StoreBoxWidget storeBox;

    public boolean isOpened() {
        return storeBox != null;
    }

    public void checkEnoughSpace() {
        if (Objects.equals(storeBox.getFirst(), storeBox.getSecond())) {
            throw new ApplicationException("Not enough empty space");
        }
    }

    public void clearWidget() {
        storeBox = null;
    }

    public Long getKnownObjectId() {
        return knownObjectId;
    }
    public void setKnownObjectId(final Long knownObjectId) {
        this.knownObjectId = knownObjectId;
    }

    public StoreBoxWidget getStoreBox() {
        return storeBox;
    }
    public StoreBoxWidget getStoreBoxOrThrow() {
        if (!isOpened()) {
            throw new ApplicationException("There is no opened heap");
        }
        return storeBox;
    }
    public void setStoreBox(final StoreBoxWidget storeBox) {
        this.storeBox = storeBox;
    }
}