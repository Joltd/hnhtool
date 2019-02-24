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
public class InboundMessageConverter {

    public ObjectNode convert(final byte[] data) {

        final DataReader reader = new DataReader(data);
        final ObjectMapper mapper = new ObjectMapper();

        final ObjectNode root = mapper.createObjectNode();

        final MessageType messageType = MessageType.of(reader.int8());
        root.put("messageType", messageType.name());

        switch (messageType) {
            case MESSAGE_TYPE_SESSION:
                final int errorCode = reader.uint8();
                root.put("errorCode", errorCode);
                break;
            case MESSAGE_TYPE_REL:
                int sequenceNumber = reader.uint16();

                final ArrayNode relList = root.putArray("rels");
                while (reader.hasNext()) {

                    final ObjectNode rel = relList.addObject();
                    rel.put("sequence", sequenceNumber);
                    final int relType = reader.uint8();
                    if (relType >= 128) {
                        final int blockLength = reader.uint16();
                        convertRel(rel, relType - 128, reader.asReader(blockLength));
                    } else {
                        convertRel(rel, relType, reader.asReader());
                    }
                    sequenceNumber++;

                }

                break;
            case MESSAGE_TYPE_ACKNOWLEDGE:
                root.put("acknowledgeSequence", reader.uint16());
                break;
            case MESSAGE_TYPE_MAP_DATA:
                root.put("pktId", reader.uint32());
                root.put("offset", reader.uint16());
                root.put("length", reader.uint16());
                root.put("data", reader.bytes());
                break;
            case MESSAGE_TYPE_OBJECT_DATA:
                convertObjectDelta(root, reader);
                break;
            case MESSAGE_TYPE_CLOSE:
                break;
        }

        return root;

    }

    private void convertRel(final ObjectNode rel, final int relTypeValue, final DataReader reader) {
        final RelType relType = RelType.of(relTypeValue);
        rel.put("relType", relType.name());
        switch (relType) {
            case REL_MESSAGE_FRAGMENT:
                // unknown how to implement
                break;
            case REL_MESSAGE_NEW_WIDGET:
            case REL_MESSAGE_WIDGET_MESSAGE:
            case REL_MESSAGE_DESTROY_WIDGET:
            case REL_MESSAGE_ADD_WIDGET:
                // ignore
                break;
            case REL_MESSAGE_MAPIV:
                // ignore
                break;
            case REL_MESSAGE_GLOBLOB:
                // ignore - weather and other graphic shit
                break;
            case REL_MESSAGE_RESOURCE_ID:
                rel.put("resourceId", reader.uint16());
                rel.put("resourceName", reader.string());
                rel.put("resourceVersion", reader.uint16());
                break;
            case REL_MESSAGE_PARTY:
                // ignore
                break;
            case REL_MESSAGE_SFX:
                // audio - long term ignore
                break;
            case REL_MESSAGE_CHARACTER_ATTRIBUTE:
                while (reader.hasNext()) {
                    rel.put("attributeName", reader.string());
                    rel.put("baseValue", reader.uint16());
                    rel.put("complexValue", reader.uint16());
                }
                break;
            case REL_MESSAGE_MUSIC:
                // audio - long term ignore
                break;
            case REL_MESSAGE_SESSION_KEY:
                rel.put("sessionKey", reader.bytes());
                break;
        }
    }

    private void convertObjectDelta(final ObjectNode objectData, final DataReader reader) {
        while (reader.hasNext()) {
            objectData.put("fl", reader.uint8());
            objectData.put("id", reader.uint32());
            objectData.put("frame", reader.int32());
            final ArrayNode deltas = objectData.putArray("deltas");
            while (true) {

                final ObjectNode delta = deltas.addObject();

                final int objectDeltaTypeValue = reader.uint8();
                final ObjectDeltaType objectDeltaType = ObjectDeltaType.of(objectDeltaTypeValue);
                delta.put("deltaType", objectDeltaType.name());

                if (objectDeltaType == ObjectDeltaType.OD_END) {
                    break;
                }

                switch (objectDeltaType) {
                    case OD_MOVE:
                        delta.put("x", reader.int32());
                        delta.put("y", reader.int32());
                        delta.put("ia", reader.uint16());
                        break;
                    case OD_RES:
                        final int resourceId = reader.uint16();
                        if ((resourceId & 0x8000) == 0) {
                            delta.put("resourceId", resourceId);
                        } else {
                            delta.put("resourceId", resourceId & ~0x8000);
                            final int resourceContentLength = reader.uint8();
                            delta.put("resourceContentLength", resourceContentLength);
                            delta.put("resourceContent", reader.bytes(resourceContentLength));
                        }
                        break;
                    case OD_LINBEG:
                        delta.put("sX", reader.int32());
                        delta.put("sY", reader.int32());
                        delta.put("vX", reader.int32());
                        delta.put("vY", reader.int32());
                        break;
                    case OD_LINSTEP:
                        int w = reader.int32();
                        if (w != -1 && (w & 0x80000000) != 0) {
                            w = reader.int32();
                        }
                        delta.put("w", w);
                        break;
                    case OD_SPEECH:
                        delta.put("zo", reader.int16() / 100F);
                        delta.put("text", reader.string());
                        break;
                    case OD_COMPOSE:
                        delta.put("resourceId", reader.uint16());
                        break;
                    case OD_ZOFF:
                        delta.put("zoff", reader.int16() / 100F);
                        break;
                    case OD_LUMIN:
                        delta.put("x", reader.int32());
                        delta.put("y", reader.int32());
                        delta.put("sz", reader.uint16());
                        delta.put("str", reader.uint8());
                        break;
                    case OD_AVATAR:
                        final ArrayNode avatarLayers = delta.putArray("avatarLayers");
                        while (true) {
                            int layerResourceId = reader.uint16();
                            if (layerResourceId == 65535) {
                                break;
                            }
                            final ObjectNode avatarLayer = avatarLayers.addObject();
                            avatarLayer.put("resourceId", layerResourceId);
                        }
                        break;
                    case OD_FOLLOW:
                        final long followId = reader.uint32();
                        delta.put("followId", followId);
                        if (followId != 0xFFFFFFFFL) {
                            delta.put("resourceId", reader.uint16());
                            delta.put("name", reader.string());
                        }
                        break;
                    case OD_HOMING:
                        final long homingId = reader.uint32();
                        delta.put("homingId", homingId);
                        if (homingId == 0xFFFFFFFFL) {
                            delta.put("homingType", "STOP");
                        } else {
                            delta.put("homingType", "START");
                            delta.put("x", reader.int32());
                            delta.put("y", reader.int32());
                            delta.put("v", reader.int32());
                        }
                        break;
                    case OD_OVERLAY:
                        int overlayId = reader.int32();
                        overlayId >>>= 1;
                        delta.put("overlayId", overlayId);
                        int overlayResourceId = reader.uint16();
                        if (overlayResourceId != 65535 && (overlayResourceId & 0x8000) != 0) {
                            overlayResourceId &= ~0x8000;
                            delta.put("overlayResourceId", overlayResourceId);
                            int overlayResourceContentLength = reader.uint8();
                            delta.put("overlayResourceContentLength", overlayResourceContentLength);
                            delta.put("overlayResourceContent", reader.bytes(overlayResourceContentLength));
                        } else {
                            delta.put("overlayResourceId", overlayResourceId);
                        }
                        break;
                    case OD_HEALTH:
                        delta.put("health", reader.uint8());
                        break;
                    case OD_BUDDY:
                        final String name = reader.string();
                        delta.put("name", name);
                        if (!name.isEmpty()) {
                            delta.put("group", reader.uint8());
                            delta.put("buddyType", reader.uint8());
                        }
                        break;
                    case OD_CMPPOSE:
                        int pfl = reader.uint8();
                        delta.put("pfl", pfl);
                        delta.put("seq", reader.uint8());
                        if ((pfl & 2) != 0) {
                            readResourceList(delta.putArray("poses"), reader);
                        }
                        if ((pfl & 4) != 0) {
                            readResourceList(delta.putArray("tposes"), reader);
                            delta.put("ttime", reader.uint8() / 10F);
                        }
                        break;
                    case OD_CMPMOD:
                        final ArrayNode modsNode = delta.putArray("mods");
                        while (true) {
                            int modId = reader.uint16();
                            if (modId == 65535) {
                                break;
                            }

                            final ObjectNode modNode = modsNode.addObject();
                            modNode.put("modId", modId);
                            readResourceList(modNode.putArray("texs"), reader);
                        }
                        break;
                    case OD_CMPEQU:
                        final ArrayNode equsNode = delta.putArray("equs");
                        while (true) {
                            final int header = reader.uint8();
                            if (header == 255) {
                                break;
                            }

                            final ObjectNode equNode = equsNode.addObject();
                            equNode.put("at", reader.string());
                            final int equResourceId = reader.uint16();
                            putResource(reader, equResourceId, equNode);
                            int ef = header & 0x80;
                            if ((ef & 128) != 0) {
                                equNode.put("x", reader.int16());
                                equNode.put("y", reader.int16());
                                equNode.put("z", reader.int16());
                            }
                        }
                        break;
                    case OD_ICON:
                        final int iconResourceId = reader.uint16();
                        delta.put("iconResourceId", iconResourceId);
                        if (iconResourceId != 65535) {
                            delta.put("iconFl", reader.uint8());
                        }
                        break;
                    case OD_RESATTR:
                        delta.put("resourceId", reader.uint16());
                        final int length = reader.uint8();
                        delta.put("resourceAttributeContentLength", length);
                        delta.put("resourceAttributeContent", reader.bytes(length));
                        break;
                }
            }

        }
    }

    private void readResourceList(final ArrayNode arrayNode, final DataReader reader) {
        while (true) {
            int resourceId = reader.uint16();
            if (resourceId == 65535) {
                return;
            }

            final ObjectNode resourceNode = arrayNode.addObject();
            putResource(reader, resourceId, resourceNode);
        }
    }

    private void putResource(final DataReader reader, int resourceId, final ObjectNode resourceNode) {
        if ((resourceId & 0x8000) != 0) {
            resourceId &= ~0x8000;
            resourceNode.put("resourceId", resourceId);
            int resourceContentLength = reader.uint8();
            resourceNode.put("resourceContentLength", resourceContentLength);
            resourceNode.put("resourceContent", reader.bytes(resourceContentLength));
        } else {
            resourceNode.put("resourceId", resourceId);
        }
    }

}
