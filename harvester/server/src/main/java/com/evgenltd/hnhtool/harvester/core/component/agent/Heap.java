package com.evgenltd.hnhtool.harvester.core.component.agent;

import com.evgenltd.hnhtools.clientapp.widgets.StoreBoxWidget;
import com.evgenltd.hnhtools.common.ApplicationException;

import java.util.Objects;

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

    public Record toRecord() {
        if (storeBox != null) {
            return new Record(knownObjectId, true, storeBox.getFirst(), storeBox.getSecond());
        } else {
            return new Record(knownObjectId, false, null, null);
        }
    }

    public static final record Record(Long knownObjectId, boolean isOpened, Integer count, Integer max) {}

}
