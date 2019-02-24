package com.evgenltd.hnhtools.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 02:01</p>
 */
public class UdpToJson {

    public ObjectNode convert(final byte[] data) {

        final DataReader reader = new DataReader(data);
        final ObjectMapper mapper = new ObjectMapper();

        final ObjectNode root = mapper.createObjectNode();

        final MessageType messageType = MessageType.of(reader.nextByte());
        root.put("messageType", messageType.name());

        switch (messageType) {
            case MESSAGE_TYPE_SESSION:

                final int errorCode = reader.nextUnsignedByte();
                root.put("errorCode", errorCode);
                break;

            case MESSAGE_TYPE_REL:

                int sequenceNumber = reader.nextUnsignedShort();

                final ArrayNode relList = root.putArray("rels");
                while (reader.hasNext()) {

                    final ObjectNode rel = relList.addObject();
                    rel.put("sequence", sequenceNumber);
                    final int relType = reader.nextUnsignedByte();
                    if (relType >= 128) {
                        final int blockLength = reader.nextUnsignedShort();
                        convertRel(rel, relType - 128, reader.nextAsReader(blockLength));
                    } else {
                        convertRel(rel, relType, reader.nextAsReader());
                    }
                    sequenceNumber++;

                }

                break;

            case MESSAGE_TYPE_ACKNOWLEDGE:
                root.put("acknowledgeSequence", reader.nextUnsignedShort());
                break;
            case MESSAGE_TYPE_MAP_DATA:
                root.put("pktId", reader.nextUnsignedInt());
                root.put("offset", reader.nextUnsignedShort());
                root.put("length", reader.nextUnsignedShort());
                root.put("data", reader.nextBytes());
                break;
            case MESSAGE_TYPE_OBJECT_DATA:
                break;
            case MESSAGE_TYPE_CLOSE:
                break;
        }

        return root;

    }

    private void convertRel(final ObjectNode rel, final int relType, final DataReader reader) {
        rel.put("relType", RelType.of(relType).name());
//        rel.put("body", reader.nextBytes());
//        switch (relType) {
//            case 0: // REL_MESSAGE_FRAGMENT
//                final int fragmentHeader = reader.nextUnsignedByte();
//                if (fragmentHeader == 0x7F) {
//
//                } else if (fragmentHeader == 0x80 || fragmentHeader == 0x81) {
//
//                } else {
//                    throw new ApplicationException("Unknown Rel message fragment type [%s]", fragmentHeader);
//                }
//                break;
//            default:
//                throw new ApplicationException("Unknown RelType [%s]", relType);
//        }
    }

}
