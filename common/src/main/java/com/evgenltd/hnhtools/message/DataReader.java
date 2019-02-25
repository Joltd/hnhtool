package com.evgenltd.hnhtools.message;

import java.nio.charset.StandardCharsets;

import static com.evgenltd.hnhtools.util.ByteUtil.unsigned;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 02:11</p>
 */
public class DataReader {

    private byte[] data;
    private int pointer = 0;

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
        return int8() & 0xFF;
    }

    public int int16() {
        return (int) (short) uint16();
    }

    public int uint16() {
        final int value = unsigned(data[pointer])
                | (unsigned(data[pointer+1]) << 8);
        pointer = pointer + 2;
        return value;
    }

    public int int32() {
        return (int) uint32();
    }

    public long uint32() {
        final long value = (long) unsigned(data[pointer])
                | (long) (unsigned(data[pointer+1]) << 8)
                | (long) (unsigned(data[pointer+2]) << 16)
                | (long) (unsigned(data[pointer+3]) << 24);
        pointer = pointer + 4;
        return value;
    }

    public String string() {
        int stringLength = 0;
        while (true) {
            final int endIndex = pointer + stringLength;
            final boolean stringEndIsFound = endIndex >= data.length || data[endIndex] == 0;
            if (stringEndIsFound) {
                break;
            } else {
                stringLength++;
            }
        }
        pointer = pointer + stringLength;
        return new String(data, pointer, stringLength, StandardCharsets.UTF_8);
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
