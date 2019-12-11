package com.evgenltd.hnhtools.clientapp.widgets;

import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

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

    Map<String, List<JsonNode>> getInfoResources();
}
