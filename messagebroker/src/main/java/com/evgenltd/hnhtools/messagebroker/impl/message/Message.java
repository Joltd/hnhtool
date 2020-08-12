package com.evgenltd.hnhtools.messagebroker.impl.message;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.message.DataReader;
import com.evgenltd.hnhtools.messagebroker.RelType;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class Message {

    private ObjectNode data;

    public Message(final ObjectNode data) {
        this.data = data;
    }

    @Nullable
    public MessageType getType() {
        return JsonUtil.asCustomFromText(data, InboundMessageConverter.MESSAGE_TYPE, MessageType::of);
    }

    // ##################################################
    // #                                                #
    // #  Session                                       #
    // #                                                #
    // ##################################################

    public boolean isSessionMessage() {
        return Objects.equals(getType(), MessageType.MESSAGE_TYPE_SESSION);
    }

    public boolean isConnectionErrorCodeOk() {
        return Objects.equals(JsonUtil.asInt(data, InboundMessageConverter.ERROR_CODE), 0);
//        switch (errorCodeNode.asInt()) {
//            case 0:
//                return ConnectionErrorCode.OK;
//            case 1:
//                return ConnectionErrorCode.INVALID_AUTH_TOKEN;
//            case 2:
//                return ConnectionErrorCode.ALREADY_LOGGED_IN;
//            case 3:
//                return ConnectionErrorCode.COULD_NOT_CONNECT;
//            case 4:
//                return ConnectionErrorCode.CLIENT_TOO_OLD;
//            case 5:
//                return ConnectionErrorCode.AUTH_TOKEN_EXPIRED;
//            default:
//                return ConnectionErrorCode.UNKNOWN;
//        }
    }

    // ##################################################
    // #                                                #
    // #  Rel                                           #
    // #                                                #
    // ##################################################

    public List<Rel> getRel() {
        return StreamSupport.stream(JsonUtil.asIterable(data, InboundMessageConverter.RELS).spliterator(), false)
                .map(Rel::new)
                .collect(Collectors.toList());
    }

    public int getRelAcknowledge() {
        return JsonUtil.asInt(data, InboundMessageConverter.ACKNOWLEDGE_SEQUENCE);
    }

    // ##################################################
    // #                                                #
    // #  Object data                                   #
    // #                                                #
    // ##################################################

    public List<ObjectData> getObjectData() {
        return StreamSupport.stream(JsonUtil.asIterable(data, InboundMessageConverter.OBJECT_DATA).spliterator(), false)
                .map(ObjectData::new)
                .collect(Collectors.toList());
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
            return data.get(InboundMessageConverter.OBJECT_ID).asLong();
        }

        public int getFrame() {
            return data.get(InboundMessageConverter.FRAME).asInt();
        }
    }

    public static final class Rel {
        private JsonNode data;

        Rel(final JsonNode data) {
            this.data = data;
        }

        public JsonNode getData() {
            return data;
        }

        public DataReader getFragment() {
            try {
                return new DataReader(data.get(InboundMessageConverter.FRAGMENT).binaryValue());
            } catch (IOException e) {
                throw new ApplicationException(e);
            }
        }

        public int getSequence() {
            return data.get(InboundMessageConverter.SEQUENCE).asInt();
        }

        public RelType getRelType() {
            final JsonNode relTypeNode = data.get(InboundMessageConverter.REL_TYPE);
            if (relTypeNode == null || relTypeNode.isNull()) {
                return null;
            }
            return RelType.valueOf(relTypeNode.asText());
        }
    }

}
