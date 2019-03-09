package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.util.ByteUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 05-03-2019 00:19</p>
 */
public class DataWriter {

    private List<Byte> data = new ArrayList<>(ByteUtil.WORD);

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
            final int bitOffset = index * ByteUtil.BIT_8;
            final long movedLongValue = value >>> bitOffset;
            final byte byteValue = (byte) (movedLongValue & ByteUtil.BYTE);
            data.add(byteValue);
        }
    }

}
