package com.evgenltd.hnhtools.util;

import com.hnh.auth.Authentication;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class UtilTest {

    @Test
    public void hexStringToByteArray() {

        final byte[] expected = Authentication.passwordHash(UUID.randomUUID().toString());
        final StringBuilder sb = new StringBuilder();
        for (byte word : expected) {
            sb.append(String.format("%02x", word));
        }

        final byte[] actual = Util.hexStringToByteArray(sb.toString());

        Assert.assertArrayEquals(expected, actual);

    }

}
