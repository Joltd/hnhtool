package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 25-02-2019 23:55</p>
 */
public class ListReader {
    
    public static void read(final ArrayNode node, final DataReader reader) {
        
        while (reader.hasNext()) {
            final Type type = Type.of(reader.uint8());
            switch (type) {
                case T_END:
                    return;
                case T_INT:
                    node.add(reader.int32());
                    break;
                case T_STR:
                    node.add(reader.string());
                    break;
                case T_COORD:
                    final ObjectNode point = node.addObject();
                    point.put("x", reader.int32());
                    point.put("y", reader.int32());
                    break;
                case T_UINT8:
                    node.add(reader.uint8());
                    break;
                case T_UINT16:
                    node.add(reader.uint16());
                    break;
                case T_INT8:
                    node.add(reader.int8());
                    break;
                case T_INT16:
                    node.add(reader.int16());
                    break;
                case T_COLOR:
                    final ObjectNode color = node.addObject();
                    color.put("red", reader.uint8());
                    color.put("green", reader.uint8());
                    color.put("blue", reader.uint8());
                    color.put("alpha", reader.uint8());
                    break;
                case T_TTOL:
                    final ArrayNode subArray = node.addArray();
                    read(subArray, reader);
                    break;
                case T_NIL:
                    node.addNull();
                    break;
                case T_UID:
                    node.add(reader.int64());
                    break;
                case T_BYTES:
                    int len = reader.uint8();
                    if ((len & 128) != 0)
                        len = reader.int32();
                    node.add(reader.bytes(len));
                    break;
                case T_FLOAT32:
                    node.add(reader.float32());
                    break;
                case T_FLOAT64:
                    node.add(reader.float64());
                    break;
                case T_FCOORD32:
                    final ObjectNode point32 = node.addObject();
                    point32.put("x", reader.float32());
                    point32.put("y", reader.float32());
                    break;
                case T_FCOORD64:
                    final ObjectNode point64 = node.addObject();
                    point64.put("x", reader.float64());
                    point64.put("y", reader.float64());
                    break;
            }
        }
        
    }
    
    enum Type {
        T_END(0),
        T_INT(1),
        T_STR(2),
        T_COORD(3),
        T_UINT8(4),
        T_UINT16(5),
        T_COLOR(6),
        T_TTOL(8),
        T_INT8(9),
        T_INT16(10),
        T_NIL(12),
        T_UID(13),
        T_BYTES(14),
        T_FLOAT32(15),
        T_FLOAT64(16),
        T_FCOORD32(18),
        T_FCOORD64(19);
        
        private int value;

        Type(final int value) {
            this.value = value;
        }

        public static Type of(int value) {
            for (final Type type : values()) {
                if (type.value == value) {
                    return type;
                }
            }

            throw new ApplicationException("Unknown Type [%s]", value);
        }
    }
    
}
