package com.evgenltd.hnhtools.clientapp.widgets;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface Widget {
    Integer getId();

    String getType();

    Integer getParentId();

    ArrayNode getArgs();
}
