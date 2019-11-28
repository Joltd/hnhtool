package com.evgenltd.hnhtool.analyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 28-02-2019 22:21</p>
 */
public class MessageFilter {

    private List<Integer> acknowledgeToSkip = new ArrayList<>();

    public boolean hideInbound(final ObjectNode root) {
        return hideMapResponse(root)
                || hideRelAcknowledge(root);
    }

    public boolean hideOutbound(final ObjectNode root) {
        return hideMapRequest(root)
                || hideObjectAcknowledge(root)
                || hideRelAcknowledge(root)
                || hideBeat(root);
    }

    //

    private boolean hideMapRequest(final ObjectNode root) {
        return root.get("messageType").asText().equals("MESSAGE_TYPE_MAP_REQUEST");
    }

    private boolean hideMapResponse(final ObjectNode root) {
        return root.get("messageType").asText().equals("MESSAGE_TYPE_MAP_DATA");
    }

    private boolean hideRelAcknowledge(final ObjectNode root) {
        return root.get("messageType").asText().equals("MESSAGE_TYPE_ACKNOWLEDGE");
    }

    private boolean hideObjectAcknowledge(final ObjectNode root) {
        return root.get("messageType").asText().equals("MESSAGE_TYPE_OBJECT_ACKNOWLEDGE");
    }

    private boolean hideBeat(final ObjectNode root) {
        return root.get("messageType").asText().equals("MESSAGE_TYPE_BEAT");
    }

    //

    private boolean hideNonFragment(final ObjectNode root) {
        return !root.get("messageType").asText().equals("MESSAGE_TYPE_REL");

//        final JsonNode rels = root.get("rels");
//        for (final JsonNode rel : rels) {
//            if (rel.get("relType").asText().equals("REL_MESSAGE_FRAGMENT")) {
//                return false;
//            }
//        }

    }

    private boolean hideGlut(final ObjectNode root) {
        if (!root.get("messageType").asText().equals("MESSAGE_TYPE_REL")) {
            return false;
        }

        final JsonNode rels = root.get("rels");
        if (rels.size() > 1) {
            return false;
        }

        for (final JsonNode rel : rels) {
            final boolean isGlut = rel.get("relType").asText().equals("REL_MESSAGE_WIDGET_MESSAGE")
                    && rel.get("name").asText().equals("glut");
            if (isGlut) {
                acknowledgeToSkip.add(rel.get("sequence").asInt());
                return true;
            }
        }

        return false;
    }

    private boolean hideKnownAcknowledge(final ObjectNode root) {
        if (!root.get("messageType").asText().equals("MESSAGE_TYPE_ACKNOWLEDGE")) {
            return false;
        }

        final int sequence = root.get("sequence").asInt();
        return acknowledgeToSkip.remove((Object) sequence);
    }

}
