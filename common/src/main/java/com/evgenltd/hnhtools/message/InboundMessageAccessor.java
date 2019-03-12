package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.baseclient.ConnectionErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 10-03-2019 16:14</p>
 */
public class InboundMessageAccessor {

    private ObjectNode data;

    public InboundMessageAccessor(final ObjectNode data) {
        this.data = data;
    }

    @Nullable
    public MessageType getType() {
        final JsonNode messageTypeNode = data.get(MessageFields.MESSAGE_TYPE);
        if (messageTypeNode == null || messageTypeNode.isNull()) {
            return null;
        }

        return MessageType.of(messageTypeNode.asText());
    }

    // ##################################################
    // #                                                #
    // #  Session                                       #
    // #                                                #
    // ##################################################

    public ConnectionErrorCode getConnectionErrorCode() {
        final JsonNode errorCodeNode = data.get(MessageFields.ERROR_CODE);
        if (errorCodeNode == null || errorCodeNode.isNull()) {
            return ConnectionErrorCode.UNKNOWN;
        }

        return ConnectionErrorCode.of(errorCodeNode.asInt());
    }

    // ##################################################
    // #                                                #
    // #  Rel                                           #
    // #                                                #
    // ##################################################

    public List<RelAccessor> getRel() {
        final JsonNode relListNode = data.get(MessageFields.RELS);
        if (relListNode == null || relListNode.isNull()) {
            return Collections.emptyList();
        }

        final List<RelAccessor> result = new ArrayList<>();
        for (final JsonNode relNode : relListNode) {
            result.add(new RelAccessor(relNode));
        }

        return result;
    }

    public int getRelAcknowledge() {
        return data.get(MessageFields.ACKNOWLEDGE_SEQUENCE).asInt();
    }

    // ##################################################
    // #                                                #
    // #  Object data                                   #
    // #                                                #
    // ##################################################

    public List<ObjectDataAccessor> getObjectData() {
        final JsonNode objectDataListNode = data.get(MessageFields.OBJECT_DATA);
        if (objectDataListNode == null || objectDataListNode.isNull()) {
            return Collections.emptyList();
        }

        final List<ObjectDataAccessor> result = new ArrayList<>();
        for (final JsonNode objectDataNode : objectDataListNode) {
            result.add(new ObjectDataAccessor(objectDataNode));
        }

        return result;
    }

    public static final class ObjectDataAccessor {
        private JsonNode data;

        ObjectDataAccessor(final JsonNode data) {
            this.data = data;
        }

        public long getId() {
            return data.get(MessageFields.ID).asLong();
        }

        public int getFrame() {
            return data.get(MessageFields.FRAME).asInt();
        }

    }

    public static final class RelAccessor {
        private JsonNode data;

        RelAccessor(final JsonNode data) {
            this.data = data;
        }

        public int getSequence() {
            return data.get(MessageFields.SEQUENCE).asInt();
        }
    }

}
