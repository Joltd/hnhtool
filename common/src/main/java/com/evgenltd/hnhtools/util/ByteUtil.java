package com.evgenltd.hnhtools.util;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 02:00</p>
 */
public class ByteUtil {

    public static int unsigned(byte value) {
        return ((int) value) & 0xFF;
    }

}
