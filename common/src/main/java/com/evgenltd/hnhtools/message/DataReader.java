package com.evgenltd.hnhtools.message;

import static com.evgenltd.hnhtools.util.ByteUtil.*;

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

    public int nextByte() {
        return data[pointer++];
    }

    public int nextUnsignedByte() {
        final int value = nextByte();
        return unsigned(value);
    }

    public int nextShort() {
        final int value = data[pointer]
                | (data[pointer+1] << 8);
        pointer = pointer + 2;
        return value;
    }

    public int nextUnsignedShort() {
        final int value = unsigned(data[pointer])
                | (unsigned(data[pointer+1]) << 8);
        pointer = pointer + 2;
        return value;
    }

    public int nextInt() {
        final int value = data[pointer]
                | (data[pointer+1] << 8)
                | (data[pointer+1] << 16)
                | (data[pointer+1] << 24);
        pointer = pointer + 4;
        return value;
    }

    public int nextUnsignedInt() {
        final int value = unsigned(data[pointer])
                | (unsigned(data[pointer+1]) << 8)
                | (unsigned(data[pointer+1]) << 16)
                | (unsigned(data[pointer+1]) << 24);
        pointer = pointer + 4;
        return value;
    }

    public byte[] nextBytes() {
        final int newLength = data.length - pointer;
        return nextBytes(newLength);
    }

    public byte[] nextBytes(final int length) {
        final byte[] bytes = new byte[length];
        System.arraycopy(data, pointer, bytes, 0, length);
        pointer = pointer + length;
        return bytes;
    }

    public DataReader nextAsReader() {
        final int newLength = data.length - pointer;
        return nextAsReader(newLength);
    }

    public DataReader nextAsReader(final int length) {
        final byte[] newData = nextBytes(length);
        return new DataReader(newData);
    }

}
