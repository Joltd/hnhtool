package com.evgenltd.hnhtool.harvester;

import com.hnh.auth.Authentication;
import org.junit.Test;

import java.util.Arrays;

public class SomeTest {

    @Test
    public void authTest() {
        final byte[] expected = Authentication.passwordHash("1234");
        System.out.println();

        String hash = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        byte[] actual = new byte[hash.length() / 2];

        for (int index = 0; index < actual.length; index++) {
            final String substring = hash.substring(index * 2, (index + 1) * 2);
            System.out.println(index + " " + substring);
            actual[index] = (byte) Integer.parseInt(substring, 16);
        }

        System.out.println(Arrays.toString(expected));
        System.out.println(Arrays.toString(actual));
    }

}
