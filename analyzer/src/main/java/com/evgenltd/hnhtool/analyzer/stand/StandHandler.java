package com.evgenltd.hnhtool.analyzer.stand;

import com.evgenltd.hnhtool.analyzer.stand.entity.GameObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StandHandler {

    private Stand stand;

    public StandHandler(final Stand stand) {
        this.stand = stand;
    }

    public void handle(final ObjectNode root) {
        final String messageType = root.get("messageType").asText();
        if (messageType.equals("MESSAGE_TYPE_REL")) {
            for (final JsonNode rel : root.get("rels")) {
                handleRel(rel);
            }
        } else if (messageType.equals("MESSAGE_TYPE_OBJECT_DATA")) {
            for (JsonNode objectData : root.get("objectData")) {
                handleObject(objectData);
            }
        }
    }

    // ##################################################
    // #                                                #
    // #  Rel Handler                                   #
    // #                                                #
    // ##################################################

    private void handleRel(final JsonNode root) {
        final String relType = root.get("relType").asText();
        if (relType.equals("REL_MESSAGE_RESOURCE_ID")) {
            stand.addResource(
                    root.get("resourceId").asLong(),
                    root.get("resourceName").asText(),
                    root.get("resourceVersion").asInt()
            );
        }
    }

    // ##################################################
    // #                                                #
    // #  Object Handler                                #
    // #                                                #
    // ##################################################

    private void handleObject(final JsonNode root) {
        final Long id = root.get("id").asLong();
        final Integer frame = root.get("frame").asInt();
        final GameObject gameObject = stand.getGameObject(id, frame);
        if (gameObject == null) {
            return;
        }

        for (final JsonNode delta : root.get("deltas")) {
            handleObjectDelta(gameObject, delta);
        }
    }

    private void handleObjectDelta(final GameObject gameObject, final JsonNode root) {
        final String deltaType = root.get("deltaType").asText();
        if (deltaType.equals("OD_MOVE")) {
            stand.move(
                    gameObject,
                    root.get("x").asLong(),
                    root.get("y").asLong(),
                    root.get("ia").asDouble() / 0xFFFF
            );
        } else if (deltaType.equals("OD_RES")) {
            stand.setResourceProxy(
                    gameObject,
                    root.get("resourceId").asLong()
            );
        }
    }

}
