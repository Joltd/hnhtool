package com.evgenltd.hnhtools.messagebroker.impl.message;

import com.evgenltd.hnhtools.message.DataReader;
import com.evgenltd.hnhtools.messagebroker.ObjectDeltaType;
import com.evgenltd.hnhtools.messagebroker.RelType;
import com.evgenltd.hnhtools.util.ByteUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class InboundMessageConverter {

    static final String MESSAGE_TYPE = "messageType";
    // session
    static final String ERROR_CODE = "errorCode";
    // common
    static final String RESOURCE_ID = "resourceId";
    // rel
    static final String FRAGMENT = "fragment";
    static final String RELS = "rels";
    static final String SEQUENCE = "sequence";
    static final String REL_TYPE = "relType";
    static final String ACKNOWLEDGE_SEQUENCE = "acknowledgeSequence";
    static final String WIDGET_ID = "id";
    static final String WIDGET_TYPE = "type";
    static final String WIDGET_PARENT_ID = "parent";
    static final String WIDGET_MESSAGE_NAME = "name";
    static final String WIDGET_ARGS = "args";
    static final String WIDGET_P_ARGS = "pArgs";
    static final String WIDGET_C_ARGS = "cArgs";
    static final String RESOURCE_NAME = "resourceName";
    static final String RESOURCE_VERSION = "resourceVersion";
    // object data
    static final String OBJECT_DATA = "objectData";
    static final String OBJECT_ID = "id";
    static final String FRAME = "frame";
    static final String DELTAS = "deltas";
    static final String DELTA_TYPE = "deltaType";
    static final String X = "x";
    static final String Y = "y";
    static final String Z = "z";
    static final String W = "w";

    public InboundMessageConverter() {}

    public static void convert(final ObjectNode root, final byte[] data) {

        final DataReader reader = new DataReader(data);

        final MessageType messageType = MessageType.of(reader.int8());
        root.put(MESSAGE_TYPE, messageType.name());

        switch (messageType) {
            case MESSAGE_TYPE_SESSION:
                final int errorCode = reader.uint8();
                root.put(ERROR_CODE, errorCode);
                break;
            case MESSAGE_TYPE_REL:
                int sequenceNumber = reader.uint16();

                final ArrayNode relList = root.putArray(RELS);
                while (reader.hasNext()) {

                    final ObjectNode rel = relList.addObject();
                    rel.put(SEQUENCE, sequenceNumber);
                    final int relType = reader.uint8();
                    if ((relType & 0x80) != 0) {
                        final int blockLength = reader.uint16();
                        convertRel(rel, relType & 0x7f, reader.asReader(blockLength));
                    } else {
                        convertRel(rel, relType, reader.asReader());
                    }
                    sequenceNumber++;

                }

                break;
            case MESSAGE_TYPE_ACKNOWLEDGE:
                root.put(ACKNOWLEDGE_SEQUENCE, reader.uint16());
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

    }

    public static void convertRel(final ObjectNode rel, final int relTypeValue, final DataReader reader) {
        final RelType relType = RelType.of(relTypeValue);
        rel.put(REL_TYPE, relType.name());
        switch (relType) {
            case REL_MESSAGE_FRAGMENT:
                rel.put(FRAGMENT, reader.bytes());
                break;
            case REL_MESSAGE_NEW_WIDGET:
                rel.put(WIDGET_ID, reader.uint16());
                rel.put(WIDGET_TYPE, reader.string());
                rel.put(WIDGET_PARENT_ID, reader.uint16());
                ByteUtil.readList(rel.putArray(WIDGET_P_ARGS), reader);
                ByteUtil.readList(rel.putArray(WIDGET_C_ARGS), reader);
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:
                rel.put(WIDGET_ID, reader.uint16());
                rel.put(WIDGET_MESSAGE_NAME, reader.string());
                ByteUtil.readList(rel.putArray(WIDGET_ARGS), reader);
                break;
            case REL_MESSAGE_DESTROY_WIDGET:
                rel.put(WIDGET_ID, reader.uint16());
                break;
            case REL_MESSAGE_ADD_WIDGET:
                rel.put(WIDGET_ID, reader.uint16());
                rel.put(WIDGET_PARENT_ID, reader.uint16());
                ByteUtil.readList(rel.putArray(WIDGET_ARGS), reader);
                break;
            case REL_MESSAGE_MAPIV:
                int mapType = reader.uint8();
                if (mapType == 0) {
                    rel.put("mapType", "INVALIDATE");
                    rel.put(X, reader.int32());
                    rel.put(Y, reader.int32());
                } else if (mapType == 1) {
                    rel.put("mapType", "TRIM");
                    rel.put("upperLeftX", reader.int32());
                    rel.put("upperLeftY", reader.int32());
                    rel.put("lowerRightX", reader.int32());
                    rel.put("lowerRightY", reader.int32());
                } else if (mapType == 2) {
                    rel.put("mapType", "TRIM_ALL");
                }
                break;
            case REL_MESSAGE_GLOBLOB:
                rel.put("inc", reader.uint8() != 0);
                final ArrayNode globalsNode = rel.putArray("globals");
                while (reader.hasNext()) {
                    final ObjectNode global = globalsNode.addObject();
                    global.put("globalType", reader.string());
                    ByteUtil.readList(global.putArray("arguments"), reader);
                }
                break;
            case REL_MESSAGE_RESOURCE_ID:
                rel.put(RESOURCE_ID, reader.uint16());
                rel.put(RESOURCE_NAME, reader.string());
                rel.put(RESOURCE_VERSION, reader.uint16());
                break;
            case REL_MESSAGE_PARTY:
                final ArrayNode partyNode = rel.putArray("party");
                while (reader.hasNext()) {
                    final ObjectNode party = partyNode.addObject();
                    int type = reader.uint8();
                    party.put("type", type);
                    if (type == 0) { // PD_LIST
                        final ArrayNode ids = party.putArray("ids");
                        while (true) {
                            long id = reader.int32();
                            if (id < 0)
                                break;
                            ids.add(id);
                        }
                    } else if (type == 1) { // PD_LEADER
                        party.put("leader", reader.int32());
                    } else if (type == 2) { // PD_MEMBER
                        party.put("member", reader.int32());
                        final boolean visible = reader.uint8() == 1;
                        party.put("visible", visible);
                        if (visible) {
                            party.put(X, reader.int32());
                            party.put(Y, reader.int32());
                        }
                        party.put("red", reader.uint8());
                        party.put("green", reader.uint8());
                        party.put("blue", reader.uint8());
                        party.put("alpha", reader.uint8());
                    }
                }
                break;
            case REL_MESSAGE_SFX:
                rel.put(RESOURCE_ID, reader.uint16());
                rel.put("volume", ((double) reader.uint16()) / 256.0);
                rel.put("speed", ((double) reader.uint16()) / 256.0);
                break;
            case REL_MESSAGE_CHARACTER_ATTRIBUTE:
                final ArrayNode attributeNode = rel.putArray("characterAttributes");
                while (reader.hasNext()) {
                    final ObjectNode attribute = attributeNode.addObject();
                    attribute.put("attributeName", reader.string());
                    attribute.put("baseValue", reader.int32());
                    attribute.put("complexValue", reader.int32());
                }
                break;
            case REL_MESSAGE_MUSIC:
                rel.put("resourceName", reader.string());
                rel.put("resourceVersion", reader.uint16());
                rel.put("loop", reader.hasNext() && (reader.uint8() != 0));
                break;
            case REL_MESSAGE_SESSION_KEY:
                rel.put("sessionKey", reader.bytes());
                break;
        }
    }

    private static void convertObjectDelta(final ObjectNode root, final DataReader reader) {
        final ArrayNode objectDataArray = root.putArray(OBJECT_DATA);
        while (reader.hasNext()) {
            final ObjectNode objectData = objectDataArray.addObject();
            objectData.put("fl", reader.uint8());
            objectData.put(OBJECT_ID, reader.uint32());
            objectData.put(FRAME, reader.int32());
            final ArrayNode deltas = objectData.putArray(DELTAS);
            while (true) {

                final ObjectNode delta = deltas.addObject();

                final int objectDeltaTypeValue = reader.uint8();
                final ObjectDeltaType objectDeltaType = ObjectDeltaType.of(objectDeltaTypeValue);
                delta.put(DELTA_TYPE, objectDeltaType.name());

                if (objectDeltaType == ObjectDeltaType.OD_END) {
                    break;
                }

                switch (objectDeltaType) {
                    case OD_MOVE:
                        delta.put(X, reader.int32());
                        delta.put(Y, reader.int32());
                        delta.put("ia", reader.uint16());
                        break;
                    case OD_RES:
                        int resourceId = reader.uint16();
                        if((resourceId & 0x8000) != 0) {
                            resourceId &= ~0x8000;
                            delta.put(RESOURCE_ID, resourceId);
                            final int resourceLength = reader.uint8();
                            delta.put("resourceContentLength", resourceLength);
                            delta.put("resourceContent", reader.bytes(resourceLength));
                        } else {
                            delta.put(RESOURCE_ID, resourceId);
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
                        delta.put(W, w);
                        break;
                    case OD_SPEECH:
                        delta.put("zo", reader.int16() / 100F);
                        delta.put("text", reader.string());
                        break;
                    case OD_COMPOSE:
                        delta.put(RESOURCE_ID, reader.uint16());
                        break;
                    case OD_ZOFF:
                        delta.put("zoff", reader.int16() / 100F);
                        break;
                    case OD_LUMIN:
                        delta.put(X, reader.int32());
                        delta.put(Y, reader.int32());
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
                            avatarLayer.put(RESOURCE_ID, layerResourceId);
                        }
                        break;
                    case OD_FOLLOW:
                        final long followId = reader.uint32();
                        delta.put("followId", followId);
                        if (followId != 0xFFFFFFFFL) {
                            delta.put(RESOURCE_ID, reader.uint16());
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
                            delta.put(X, reader.int32());
                            delta.put(Y, reader.int32());
                            delta.put("v", reader.int32());
                        }
                        break;
                    case OD_OVERLAY:
                        delta.put("overlayId", reader.int32() >>> 1);
                        int overlayResourceId = reader.uint16();
                        if (overlayResourceId == 65535) {
                            delta.put(RESOURCE_ID, overlayResourceId);
                        } else {
                            if ((overlayResourceId & 0x8000) != 0) {
                                overlayResourceId &= ~0x8000;
                                delta.put(RESOURCE_ID, overlayResourceId);
                                final int length111 = reader.uint8();
                                delta.put("resourceContentLength", length111);
                                delta.put("resourceContent", reader.bytes(length111));
                            }
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
                            final ArrayNode poses = delta.putArray("poses");
                            readResource(reader, poses);
                        }
                        if ((pfl & 4) != 0) {
                            final ArrayNode tposes = delta.putArray("tposes");
                            readResource(reader, tposes);
                            delta.put("ttime", reader.uint8() / 10.0f);
                        }
                        break;
                    case OD_CMPMOD:
                        final ArrayNode mods = delta.putArray("mods");
                        while (true) {
                            final ObjectNode mod = mods.addObject();
                            int modId = reader.uint16();
                            mod.put("modId", modId);
                            if (modId == 65535)
                                break;
                            final ArrayNode modResources = mod.putArray("resources");
                            readResource(reader, modResources);
                        }
                        break;
                    case OD_CMPEQU:
                        final ArrayNode equs = delta.putArray("equs");
                        while (true) {
                            final ObjectNode equ = equs.addObject();
                            int h = reader.uint8();
                            equ.put("h", h);
                            if (h == 255)
                                break;
                            int ef = h & 0x80;
                            equ.put("at", reader.string());
                            int equResourceId = reader.uint16();
                            if ((equResourceId & 0x8000) != 0) {
                                equResourceId &= ~0x8000;
                                equ.put(RESOURCE_ID, equResourceId);
                                final int equResourceLength = reader.uint8();
                                equ.put("resourceContentLength", equResourceLength);
                                equ.put("resourceContent", reader.bytes(equResourceLength));
                            }
                            if ((ef & 128) != 0) {
                                equ.put(X, reader.int16());
                                equ.put(Y, reader.int16());
                                equ.put(Z, reader.int16());
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
                        delta.put(RESOURCE_ID, reader.uint16());
                        final int length = reader.uint8();
                        delta.put("resourceAttributeContentLength", length);
                        delta.put("resourceAttributeContent", reader.bytes(length));
                        break;
                }
            }

        }
    }

    private static void readResource(final DataReader reader, final ArrayNode resourceList) {
        while (true) {
            final ObjectNode resource = resourceList.addObject();
            int poseResourceId = reader.uint16();
            if (poseResourceId == 65535) {
                resource.put(RESOURCE_ID, poseResourceId);
                return;
            }
            if ((poseResourceId & 0x8000) != 0) {
                poseResourceId &= ~0x8000;
                resource.put(RESOURCE_ID, poseResourceId);
                final int poseResourceLength = reader.uint8();
                resource.put("resourceContentLength", poseResourceLength);
                resource.put("resourceContent", reader.bytes(poseResourceLength));
            }
        }
    }

}
