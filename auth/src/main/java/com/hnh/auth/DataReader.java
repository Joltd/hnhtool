package com.hnh.auth;

import java.nio.charset.StandardCharsets;

@SuppressWarnings({"unused", "WeakerAccess"})
final class DataReader {

    private static final int BIT_8 = 8;
    private static final int BYTE = 0xFF; // 8

    private byte[] data;
    private int pointer = 0;

    DataReader(final byte[] data) {
        this.data = data;
    }

    boolean hasNext() {
        return pointer < data.length;
    }

    int int8() {
        return data[pointer++];
    }

    int uint8() {
        return data[pointer++] & BYTE;
    }

    int int16() {
        return (short) read(2);
    }

    int uint16() {
        return (int) read(2);
    }

    int int32() {
        return (int) read(4);
    }

    long uint32() {
        return read(4);
    }

    long int64() {
        return read(8);
    }

    private long read(final int length) {
        long value = 0;
        for (int index = 0; index < length; index++) {
            final long unsignedLongValue = Byte.toUnsignedLong(data[pointer + index]);
            final int bitOffset = index * BIT_8;
            value |= unsignedLongValue << bitOffset;
        }
        pointer += length;
        return value;
    }

    float float32() {
        return Float.intBitsToFloat(int32());
    }

    double float64() {
        return Double.longBitsToDouble(int64());
    }

    String string() {
        int stringLength = 0;
        while (true) {
            if (data[stringLength + pointer] == 0) {
                String value = new String(data, pointer, stringLength, StandardCharsets.UTF_8);
                pointer += stringLength + 1;
                return value;
            }
            stringLength++;
        }
    }

    byte[] bytes() {
        final int newLength = data.length - pointer;
        return bytes(newLength);
    }

    byte[] bytes(final int length) {
        final byte[] bytes = new byte[length];
        System.arraycopy(data, pointer, bytes, 0, length);
        pointer = pointer + length;
        return bytes;
    }

    DataReader asReader() {
        final int newLength = data.length - pointer;
        return asReader(newLength);
    }

    private DataReader asReader(final int length) {
        final byte[] newData = bytes(length);
        return new DataReader(newData);
    }

}
