package com.evgenltd.hnhtools.clientapp.widgets;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 19-11-2019 00:13</p>
 */
public interface Widget {
    Integer getId();

    String getType();

    Integer getParentId();

    ArrayNode getArgs();
}
