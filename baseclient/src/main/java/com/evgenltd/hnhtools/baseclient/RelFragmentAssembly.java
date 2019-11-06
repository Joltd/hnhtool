package com.evgenltd.hnhtools.baseclient;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.message.*;
import com.evgenltd.hnhtools.util.ByteUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 06-11-2019 23:03</p>
 */
public class RelFragmentAssembly {

    private Builder builder = new Builder();

    public boolean append(final InboundMessageAccessor.RelAccessor data) {
        final DataReader reader = data.getFragment();
        return builder.build(reader);
    }

    public byte[] convert(final InboundMessageAccessor.RelAccessor relAccessor) {
        final int type = builder.getType();
        final byte[] data = builder.getData();
        builder.clear();
        InboundMessageConverter.convertRel((ObjectNode) relAccessor.getData(), type, new DataReader(data));

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

        public int getType() {
            return type;
        }

        public byte[] getData() {
            return composition;
        }

        void clear() {
            type = 0;
            composition = null;
        }
    }

}
