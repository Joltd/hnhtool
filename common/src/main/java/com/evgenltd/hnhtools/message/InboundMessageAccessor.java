package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.baseclient.ConnectionErrorCode;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

        public JsonNode getData() {
            return data;
        }

        public long getId() {
            return data.get(MessageFields.OBJECT_ID).asLong();
        }

        public int getFrame() {
            return data.get(MessageFields.FRAME).asInt();
        }

        public List<ObjectDataDeltaAccessor> getDeltas() {
            final JsonNode objectDataDeltaListNode = data.get(MessageFields.DELTAS);
            if (objectDataDeltaListNode == null || objectDataDeltaListNode.isNull()) {
                return Collections.emptyList();
            }

            final List<ObjectDataDeltaAccessor> result = new ArrayList<>();
            for (final JsonNode objectDataDeltaNode : objectDataDeltaListNode) {
                result.add(new ObjectDataDeltaAccessor(objectDataDeltaNode));
            }

            return result;
        }

    }

    public static final class ObjectDataDeltaAccessor {
        private JsonNode data;

        ObjectDataDeltaAccessor(final JsonNode data) {
            this.data = data;
        }

        public JsonNode getData() {
            return data;
        }

        public ObjectDeltaType getType() {
            final JsonNode deltaTypeNode = data.get(MessageFields.DELTA_TYPE);
            if (deltaTypeNode == null || deltaTypeNode.isNull()) {
                return null;
            }
            return ObjectDeltaType.valueOf(deltaTypeNode.asText());
        }

        public int getX() {
            return data.get(MessageFields.X).asInt();
        }

        public int getY() {
            return data.get(MessageFields.Y).asInt();
        }

        public int getResourceId() {
            return data.get(MessageFields.RESOURCE_ID).asInt();
        }

    }

    public static final class RelAccessor {
        private JsonNode data;

        public RelAccessor(final JsonNode data) {
            this.data = data;
        }

        public JsonNode getData() {
            return data;
        }

        public DataReader getFragment() {
            try {
                return new DataReader(data.get(MessageFields.FRAGMENT).binaryValue());
            } catch (IOException e) {
                throw new ApplicationException(e);
            }
        }

        public int getSequence() {
            return data.get(MessageFields.SEQUENCE).asInt();
        }

        public RelType getRelType() {
            final JsonNode relTypeNode = data.get(MessageFields.REL_TYPE);
            if (relTypeNode == null || relTypeNode.isNull()) {
                return null;
            }
            return RelType.valueOf(relTypeNode.asText());
        }

        public int getWidgetId() {
            return data.get(MessageFields.WIDGET_ID).asInt();
        }

        public String getWidgetType() {
            return data.get(MessageFields.WIDGET_TYPE).asText();
        }

        public String getWidgetMessageName() {
            return data.get(MessageFields.WIDGET_MESSAGE_NAME).asText();
        }

        public int getResourceId() {
            return data.get(MessageFields.RESOURCE_ID).asInt();
        }

        public String getResourceName() {
            return data.get(MessageFields.RESOURCE_NAME).asText();
        }

        private RelArgsAccessor getArgs(final String field) {
            return new RelArgsAccessor(data.get(field));
        }

        public RelArgsAccessor getCArgs() {
            return getArgs(MessageFields.WIDGET_C_ARGS);
        }

    }

    public static final class RelArgsAccessor {

        private JsonNode data;
        private Iterator<JsonNode> iterator;

        RelArgsAccessor(final JsonNode data) {
            this.data = data;
            iterator = data.iterator();
        }

        public void next() {
            iterator.next();
        }

        public Integer asInt() {
            final JsonNode next = iterator.next();
            return next.asInt();
        }

        public Long asLong() {
            return iterator.next().asLong();
        }

    }

}
