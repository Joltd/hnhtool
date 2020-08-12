package com.evgenltd.hnhtools.messagebroker.impl;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.message.DataReader;
import com.evgenltd.hnhtools.message.DataWriter;
import com.evgenltd.hnhtools.messagebroker.impl.message.InboundMessageConverter;
import com.evgenltd.hnhtools.messagebroker.impl.message.Message;
import com.evgenltd.hnhtools.messagebroker.impl.message.MessageType;
import com.evgenltd.hnhtools.util.ByteUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class RelFragmentAssembly {

    private Builder builder = new Builder();

    boolean append(final Message.Rel data) {
        final DataReader reader = data.getFragment();
        return builder.build(reader);
    }

    byte[] convert(final ObjectNode rel) {
        final int type = builder.getType();
        final byte[] data = builder.getData();
        builder.clear();
        InboundMessageConverter.convertRel(rel, type, new DataReader(data));

        final DataWriter writer = new DataWriter();
        writer.adduint8(MessageType.MESSAGE_TYPE_REL.getValue());
        writer.adduint16(ByteUtil.BIT_16);
        writer.adduint8(type);
        writer.addbytes(data);
        return writer.bytes();
    }

    private static class Builder {
        private int type;
        private byte[] composition;

        boolean build(final DataReader reader) {
            final int head = reader.uint8();
            if ((head & 0x80) == 0) {
                if (composition != null) {
                    throw new ApplicationException("Another fragment in build progress");
                }
                composition = reader.bytes();
                type = head;
            } else if (head == 0x80 || head == 0x81) {
                final byte[] fragment = reader.bytes();
                final byte[] newComposition = new byte[composition.length + fragment.length];
                System.arraycopy(composition, 0, newComposition, 0, composition.length);
                System.arraycopy(fragment, 0, newComposition, composition.length, fragment.length);
                composition = newComposition;
                return head == 0x81;
            } else {
                throw new ApplicationException("Unknown rel fragment type [%s]", head);
            }
            return false;
        }

        int getType() {
            return type;
        }

        byte[] getData() {
            return composition;
        }

        void clear() {
            type = 0;
            composition = null;
        }
    }

}
