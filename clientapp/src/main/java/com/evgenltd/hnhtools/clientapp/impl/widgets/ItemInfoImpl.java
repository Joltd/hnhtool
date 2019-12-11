package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.widgets.ItemInfo;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-12-2019 01:32</p>
 */
public final class ItemInfoImpl implements ItemInfo {

    private Long resourceId;
    private String resource;
    private List<JsonNode> args = new ArrayList<>();
    private List<ItemInfoImpl> itemInfoList = new ArrayList<>();

    public Long getResourceId() {
        return resourceId;
    }
    void setResourceId(final Long resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String getResource() {
        return resource;
    }
    public void setResource(final String resource) {
        this.resource = resource;
    }

    public List<JsonNode> getArgs() {
        return args;
    }
    void setArgs(final List<JsonNode> args) {
        this.args = args;
    }

    @Override
    public List<ItemInfoImpl> getItemInfoList() {
        return itemInfoList;
    }
    void setItemInfoList(final List<ItemInfoImpl> itemInfoList) {
        this.itemInfoList = itemInfoList;
    }
}
