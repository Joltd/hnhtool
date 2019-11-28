package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester_old.common.ResourceConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 15-04-2019 23:41</p>
 */
public class JacksonTest {

    @Test
    public void testResources() {
        ResourceConstants.isStack("123");
    }

    @Test
    public void test() throws Exception {

        final ObjectMapper objectMapper = new ObjectMapper();

        final ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.add(1234);
        final ArrayNode sub = arrayNode.addArray();
        sub.add("qwert");
        final ArrayNode subSub = sub.addArray();
        subSub.add(true);
        subSub.add(12.45);

        final List list = objectMapper.readValue(arrayNode.toString(), List.class);
        System.out.println();

    }

    private List convert(ArrayNode arrayNode) {
        final List list = new ArrayList();
        for (JsonNode node : arrayNode) {

        }
        return list;
    }

}
