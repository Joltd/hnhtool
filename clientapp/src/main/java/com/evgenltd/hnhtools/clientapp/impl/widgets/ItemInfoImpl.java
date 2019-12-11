package com.evgenltd.hnhtools.clientapp.impl.widgets;

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
public class ItemInfoImpl {

    private Long resourceId;
    private String resource;
    private JsonNode args;
    private final List<ItemInfoImpl> subInfo = new ArrayList<>();

}
