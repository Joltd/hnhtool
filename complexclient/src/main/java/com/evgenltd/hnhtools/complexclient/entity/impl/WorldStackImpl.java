package com.evgenltd.hnhtools.complexclient.entity.impl;

import com.evgenltd.hnhtools.complexclient.entity.WorldStack;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-04-2019 22:29</p>
 */
public class WorldStackImpl implements WorldStack {

    private Integer id;
    private Long parentObjectId;
    private Integer windowId;
    private Integer count;
    private Integer max;

    public WorldStackImpl(final Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Long getParentObjectId() {
        return parentObjectId;
    }
    public void setParentObjectId(final Long parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    public Integer getWindowId() {
        return windowId;
    }
    public void setWindowId(final Integer windowId) {
        this.windowId = windowId;
    }

    @Override
    public Integer getCount() {
        return count;
    }
    public void setCount(final Integer count) {
        this.count = count;
    }

    @Override
    public Integer getMax() {
        return max;
    }
    public void setMax(final Integer max) {
        this.max = max;
    }
}
