package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.msg.DataReader;
import com.evgenltd.hnhtools.util.ByteUtil;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 02:11</p>
 */
public class DataReaderDebug extends DataReader {

    public DataReaderDebug(final byte[] data) {
        super(data);
    }

    @Override
    public int int8() {
        final int value = super.int8();
        print(1, value);
        return value;
    }

    @Override
    public int uint8() {
        final int value = super.uint8();
        print(1, value);
        return value;
    }

    @Override
    public int int16() {
        final int value = super.int16();
        print(2, value);
        return value;
    }

    @Override
    public int uint16() {
        final int value = super.uint16();
        print(2, value);
        return value;
    }

    @Override
    public int int32() {
        final int value = super.int32();
        print(4, value);
        return value;
    }

    @Override
    public long uint32() {
        final long value = super.uint32();
        print(4, value);
        return value;
    }

    @Override
    public long int64() {
        final long value = super.int64();
        print(8, value);
        return value;
    }

    @Override
    public String string() {
        final String value = super.string();
        print(value.length(), value);
        return value;
    }

    @Override
    public byte[] bytes(final int length) {
        final byte[] value = super.bytes(length);
        print(length, ByteUtil.bytesToString(value));
        return value;
    }

    private void print(final int length, final Object value) {
        final byte[] sub = new byte[length];
        final int prevPointer = pointer - length;
        System.arraycopy(data, prevPointer, sub, 0, length);
        System.out.printf("%s - %s - %s\n", prevPointer, ByteUtil.bytesToString(sub), value);
    }

}
