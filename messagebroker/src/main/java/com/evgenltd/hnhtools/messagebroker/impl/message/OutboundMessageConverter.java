package com.evgenltd.hnhtools.messagebroker.impl.message;

import com.evgenltd.hnhtools.message.DataReader;
import com.evgenltd.hnhtools.util.ByteUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class OutboundMessageConverter {

    public OutboundMessageConverter() {}

    public static void convert(final ObjectNode root, final byte[] data) {
        final DataReader reader = new DataReader(data);

        final MessageType messageType = MessageType.of(reader.int8());
        root.put("messageType", messageType.name());

        switch (messageType) {
            case MESSAGE_TYPE_BEAT:
                break;
            case MESSAGE_TYPE_ACKNOWLEDGE:
                root.put("sequence", reader.uint16());
                break;
            case MESSAGE_TYPE_MAP_REQUEST:
                root.put("x", reader.int32());
                root.put("y", reader.int32());
                break;
            case MESSAGE_TYPE_SESSION:
                root.put("?", reader.uint16());
                root.put("client", reader.string());
                root.put("protocolVersion", reader.uint16());
                root.put("username", reader.string());
                final int cookieLength = reader.uint16();
                root.put("cookieLength", cookieLength);
                root.put("cookie", reader.bytes(cookieLength));
                ByteUtil.readList(root.putArray("arguments"), reader);
                break;
            case MESSAGE_TYPE_REL:
                root.put("sequence", reader.uint16());
                root.put("type", reader.uint8());
                root.put("id", reader.uint16());
                root.put("name", reader.string());
                ByteUtil.readList(root.putArray("arguments"), reader);
                break;
            case MESSAGE_TYPE_OBJECT_ACKNOWLEDGE:
                root.put("id", reader.uint32());
                root.put("frame", reader.int32());
                break;
            case MESSAGE_TYPE_CLOSE:
                break;
        }
    }

}
