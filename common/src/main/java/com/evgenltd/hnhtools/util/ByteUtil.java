package com.evgenltd.hnhtools.util;


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

    public static int unsigned(byte value) {
        return ((int) value) & BYTE;
    }

    public static byte signed(int value) {
        return (byte) value;
    }

    public static String bytesToString(final byte[] data) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}
