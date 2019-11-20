package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
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
@Deprecated
public class Message {

    private ObjectNode data;

    public Message(final ObjectNode data) {
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

    public ObjectNode getData() {
        return data;
    }

    // ##################################################
    // #                                                #
    // #  Session                                       #
    // #                                                #
    // ##################################################

    public ConnectionErrorCode getConnectionErrorCode() {
        final JsonNode errorCodeNode = data.get(MessageFields.ERROR_CODE);
        if (errorCodeNode == null || errorCodeNode.isNull()) {
            return null;
        }

        switch (errorCodeNode.asInt()) {
            case 0:
                return ConnectionErrorCode.OK;
            case 1:
                return ConnectionErrorCode.INVALID_AUTH_TOKEN;
            case 2:
                return ConnectionErrorCode.ALREADY_LOGGED_IN;
            case 3:
                return ConnectionErrorCode.COULD_NOT_CONNECT;
            case 4:
                return ConnectionErrorCode.CLIENT_TOO_OLD;
            case 5:
                return ConnectionErrorCode.AUTH_TOKEN_EXPIRED;
            default:
                return ConnectionErrorCode.UNKNOWN;
        }
    }

    public enum ConnectionErrorCode {
        OK,
        INVALID_AUTH_TOKEN,
        ALREADY_LOGGED_IN,
        COULD_NOT_CONNECT,
        CLIENT_TOO_OLD,
        AUTH_TOKEN_EXPIRED,
        UNKNOWN
    }

    // ##################################################
    // #                                                #
    // #  Rel                                           #
    // #                                                #
    // ##################################################

    public List<Rel> getRel() {
        final JsonNode relListNode = data.get(MessageFields.RELS);
        if (relListNode == null || relListNode.isNull()) {
            return Collections.emptyList();
        }

        final List<Rel> result = new ArrayList<>();
        for (final JsonNode relNode : relListNode) {
            result.add(new Rel(relNode));
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

    public List<ObjectData> getObjectData() {
        final JsonNode objectDataListNode = data.get(MessageFields.OBJECT_DATA);
        if (objectDataListNode == null || objectDataListNode.isNull()) {
            return Collections.emptyList();
        }

        final List<ObjectData> result = new ArrayList<>();
        for (final JsonNode objectDataNode : objectDataListNode) {
            result.add(new ObjectData(objectDataNode));
        }

        return result;
    }

    public static final class ObjectData {
        private JsonNode data;

        ObjectData(final JsonNode data) {
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

        public List<Delta> getDeltas() {
            final JsonNode objectDataDeltaListNode = data.get(MessageFields.DELTAS);
            if (objectDataDeltaListNode == null || objectDataDeltaListNode.isNull()) {
                return Collections.emptyList();
            }

            final List<Delta> result = new ArrayList<>();
            for (final JsonNode objectDataDeltaNode : objectDataDeltaListNode) {
                result.add(new Delta(objectDataDeltaNode));
            }

            return result;
        }

        public static final class Delta {
            private JsonNode data;

            Delta(final JsonNode data) {
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

            public int getW() {
                return data.get(MessageFields.W).asInt();
            }

            public long getResourceId() {
                return data.get(MessageFields.RESOURCE_ID).asLong();
            }

        }
    }

    public static final class Rel {
        private JsonNode data;

        public Rel(final JsonNode data) {
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

        //

        public long getResourceId() {
            return data.get(MessageFields.RESOURCE_ID).asLong();
        }
        public String getResourceName() {
            return data.get(MessageFields.RESOURCE_NAME).asText();
        }

        //

        public int getWidgetId() {
            return data.get(MessageFields.WIDGET_ID).asInt();
        }
        public String getWidgetType() {
            return data.get(MessageFields.WIDGET_TYPE).asText();
        }
        public String getWidgetMessageName() {
            return data.get(MessageFields.WIDGET_MESSAGE_NAME).asText();
        }
        public int getWidgetParentId() {
            return data.get(MessageFields.WIDGET_PARENT_ID).asInt();
        }

        private RelArgsAccessor getArgs(final String field) {
            return new RelArgsAccessor(data.get(field));
        }
        private String getArgsAsString(final String field) {
            final JsonNode jsonNode = data.get(field);
            return jsonNode.toString();
        }

        public RelArgsAccessor getArgs() {
            return getArgs(MessageFields.WIDGET_ARGS);
        }
        public String getArgsAsString() {
            return getArgsAsString(MessageFields.WIDGET_ARGS);
        }

        public RelArgsAccessor getPArgs() {
            return getArgs(MessageFields.WIDGET_P_ARGS);
        }

        public RelArgsAccessor getCArgs() {
            return getArgs(MessageFields.WIDGET_C_ARGS);
        }

        public static final class RelArgsAccessor {

            private Iterator<JsonNode> iterator;

            RelArgsAccessor(final JsonNode data) {
                iterator = data.iterator();
            }

            public boolean hasNext() {
                return iterator.hasNext();
            }

            public void skip() {
                iterator.next();
            }

            public Integer nextInt() {
                final JsonNode next = iterator.next();
                return next.asInt();
            }

            public Long nextLong() {
                return iterator.next().asLong();
            }

            public String nextString() {
                return iterator.next().asText();
            }

            public IntPoint nextPoint() {
                final JsonNode nodePoint = iterator.next();
                return new IntPoint(
                        nodePoint.get("x").asInt(),
                        nodePoint.get("y").asInt()
                );
            }

            public JsonNode nextNode() {
                return iterator.next();
            }

        }
    }

}
