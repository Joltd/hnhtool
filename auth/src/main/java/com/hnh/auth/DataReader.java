package com.hnh.auth;

import java.nio.charset.StandardCharsets;

public class DataReader {

    public static final int BIT_8 = 8;
    public static final int BYTE = 0xFF; // 8

    protected byte[] data;
    protected int pointer = 0;

    public DataReader(final byte[] data) {
        this.data = data;
    }

    public boolean hasNext() {
        return pointer < data.length;
    }

    public int int8() {
        return data[pointer++];
    }

    public int uint8() {
        return data[pointer++] & BYTE;
    }

    public int int16() {
        return (short) read(2);
    }

    public int uint16() {
        return (int) read(2);
    }

    public int int32() {
        return (int) read(4);
    }

    public long uint32() {
        return read(4);
    }

    public long int64() {
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

    public float float32() {
        return Float.intBitsToFloat(int32());
    }

    public double float64() {
        return Double.longBitsToDouble(int64());
    }

    public String string() {
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

    public byte[] bytes() {
        final int newLength = data.length - pointer;
        return bytes(newLength);
    }

    public byte[] bytes(final int length) {
        final byte[] bytes = new byte[length];
        System.arraycopy(data, pointer, bytes, 0, length);
        pointer = pointer + length;
        return bytes;
    }

    public DataReader asReader() {
        final int newLength = data.length - pointer;
        return asReader(newLength);
    }

    public DataReader asReader(final int length) {
        final byte[] newData = bytes(length);
        return new DataReader(newData);
    }

}
