package com.evgenltd.hnhtools.clientapp.widgets;

import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 23:17</p>
 */
public interface ItemWidget extends Widget {

    String getResource();

    IntPoint getPosition();

    ArrayNode getLabel();

}
