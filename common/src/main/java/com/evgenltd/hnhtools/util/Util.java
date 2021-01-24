package com.evgenltd.hnhtools.util;

import com.evgenltd.hnhtools.common.Assert;

public class Util {

    public static byte[] hexStringToByteArray(final String value) {
        if (Assert.isEmpty(value)) {
            return new byte[] {};
        }

        final byte[] result = new byte[value.length() / 2];
        for (int index = 0; index < value.length(); index += 2) {
            final String word = value.substring(index, index + 2);
            result[index / 2] = (byte) Integer.parseInt(word, 16);
        }

        return result;
    }

}
