package com.hnh.auth;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataWriter {

    private static final int BIT_8 = 8;

    private static final int BYTE = 0xFF; // 8
    private static final int WORD = 0xFF_FF; // 16

    private List<Byte> data = new ArrayList<>(WORD);

    // ##################################################
    // #                                                #
    // #  Write                                         #
    // #                                                #
    // ##################################################


    public void adduint8(final int value) {
        data.add((byte) value);
    }

    public void adduint16(final int value) {
        write(value, 2);
    }

    public void addint32(final int value) {
        write(value, 4);
    }

    public void adduint32(final long value) {
        write(value, 4);
    }

    public void addint64(final long value) {
        write(value, 8);
    }

    public void addfloat32(final float value) {
        addint32(Float.floatToIntBits(value));
    }

    public void addfloat64(final double value) {
        addint64(Double.doubleToLongBits(value));
    }

    public void addString(final String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        addbytes(bytes);
        adduint8(0);
    }

    public void addbytes(final byte[] value) {
        for (final byte b : value) {
            data.add(b);
        }
    }

    public void addlist() {

    }

    public byte[] bytes() {
        final byte[] result = new byte[data.size()];
        for (int index = 0; index < data.size(); index++) {
            result[index] = data.get(index);
        }
        return result;
    }

    private void write(final long value, final int length) {
        for (int index = 0; index < length; index++) {
            final int bitOffset = index * BIT_8;
            final long movedLongValue = value >>> bitOffset;
            final byte byteValue = (byte) (movedLongValue & BYTE);
            data.add(byteValue);
        }
    }

}
