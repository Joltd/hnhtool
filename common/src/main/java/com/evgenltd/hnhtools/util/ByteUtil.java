package com.evgenltd.hnhtools.util;


import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.message.DataReader;
import com.evgenltd.hnhtools.message.DataWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.awt.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 02:00</p>
 */
public class ByteUtil {

    public static final int BIT_8 = 8;
    public static final int BIT_16 = 16;
    public static final int BIT_32 = 32;

    public static final int BYTE = 0xFF; // 8
    public static final int WORD = 0xFF_FF; // 16
    public static final int DWORD = 0xFF_FF_FF_FF; // 32

    /**
     * <p>Passed value will be adjusted to a range of 0..0xFF_FF.
     * Negative values will be adjusted by a 0xFF_FF offset</p>
     * @param value target value
     * @return 0..0xFF_FF representation
     */
    public static int toShort(final int value) {
        final int shortValue = value % WORD;
        return shortValue >= 0
                ? shortValue
                : shortValue + WORD;
    }

    public static void readList(final ArrayNode node, final DataReader reader) {

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
                    readList(subArray, reader);
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

    public static void writeList(final DataWriter writer, final Object... args) {
        for(Object value : args) {
            if(value == null) {
                writer.adduint8(Type.T_NIL.getValue());
            } else if(value instanceof Integer) {
                writer.adduint8(Type.T_INT.getValue());
                final Integer intValue = (Integer) value;
                writer.addint32(intValue);
            } else if(value instanceof String) {
                writer.adduint8(Type.T_STR.getValue());
                writer.addString((String) value);
            } else if(value instanceof IntPoint) {
                writer.adduint8(Type.T_COORD.getValue());
                final IntPoint point = (IntPoint) value;
                writer.addint32(point.getX());
                writer.addint32(point.getY());
            } else if(value instanceof byte[]) {
                byte[] byteArray = (byte[]) value;
                writer.adduint8(Type.T_BYTES.getValue());
                if(byteArray.length < 128) {
                    writer.adduint8(byteArray.length);
                } else {
                    writer.adduint8(0x80);
                    writer.addint32(byteArray.length);
                }
                writer.addbytes(byteArray);
            } else if(value instanceof Color) {
                writer.adduint8(Type.T_COLOR.getValue());
                final Color color = (Color) value;
                writer.adduint8(color.getRed());
                writer.adduint8(color.getGreen());
                writer.adduint8(color.getBlue());
                writer.adduint8(color.getAlpha());
            } else if(value instanceof Float) {
                writer.adduint8(Type.T_FLOAT32.getValue());
                final Float floatValue = (Float) value;
                writer.addfloat32(floatValue);
            } else if(value instanceof Double) {
                writer.adduint8(Type.T_FLOAT64.getValue());
                final Double doubleValue = (Double) value;
                writer.addfloat64(doubleValue.floatValue());
            } else {
                throw new ApplicationException("Unable to write list element, type [%s]",  value.getClass());
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

        public int getValue() {
            return value;
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
