package com.evgenltd.hnhtools.message;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class DataReaderTest {

    @Test
    public void test8() {
        checkNumber(0, DataReader::int8, 0x0);
        checkNumber(127, DataReader::int8, 0x7F);
        checkNumber(-128, DataReader::int8, 0x80);
        checkNumber(-1, DataReader::int8, 0xFF);

        checkNumber(0, DataReader::uint8, 0x0);
        checkNumber(127, DataReader::uint8, 0x7F);
        checkNumber(128, DataReader::uint8, 0x80);
        checkNumber(255, DataReader::uint8, 0xFF);
    }

    @Test
    public void test16() {
        checkNumber(0, DataReader::int16, 0x0, 0x0);
        checkNumber(127, DataReader::int16, 0x7F, 0x0);
        checkNumber(128, DataReader::int16, 0x80, 0x0);
        checkNumber(255, DataReader::int16, 0xFF, 0x0);
        checkNumber(256, DataReader::int16, 0x0, 0x1);
        checkNumber(32767, DataReader::int16, 0xFF, 0x7F);
        checkNumber(-32513, DataReader::int16, 0xFF, 0x80);
        checkNumber(-1, DataReader::int16, 0xFF, 0xFF);

        checkNumber(0, DataReader::uint16, 0x0, 0x0);
        checkNumber(127, DataReader::uint16, 0x7F, 0x0);
        checkNumber(128, DataReader::uint16, 0x80, 0x0);
        checkNumber(255, DataReader::uint16, 0xFF, 0x0);
        checkNumber(256, DataReader::uint16, 0x0, 0x1);
        checkNumber(43775, DataReader::uint16, 0xFF, 0xAA);
        checkNumber(65535, DataReader::uint16, 0xFF, 0xFF);
    }

    @Test
    public void test32() {
        checkNumber(0, DataReader::int32, 0x0, 0x0, 0x0, 0x0);
        checkNumber(128, DataReader::int32, 0x80, 0x00, 0x00, 0x00);
        checkNumber(255, DataReader::int32, 0xFF, 0x00, 0x00, 0x00);
        checkNumber(33023, DataReader::int32, 0xFF, 0x80, 0x00, 0x00);
        checkNumber(65535, DataReader::int32, 0xFF, 0xFF, 0x00, 0x00);
        checkNumber(8454143, DataReader::int32, 0xFF, 0xFF, 0x80, 0x00);
        checkNumber(16777215, DataReader::int32, 0xFF, 0xFF, 0xFF, 0x00);
        checkNumber(-2130706433, DataReader::int32, 0xFF, 0xFF, 0xFF, 0x80);
        checkNumber(-1, DataReader::int32, 0xFF, 0xFF, 0xFF, 0xFF);

        checkNumber(0L, DataReader::uint32, 0x0, 0x0, 0x0, 0x0);
        checkNumber(128L, DataReader::uint32, 0x80, 0x00, 0x00, 0x00);
        checkNumber(255L, DataReader::uint32, 0xFF, 0x00, 0x00, 0x00);
        checkNumber(33023L, DataReader::uint32, 0xFF, 0x80, 0x00, 0x00);
        checkNumber(65535L, DataReader::uint32, 0xFF, 0xFF, 0x00, 0x00);
        checkNumber(8454143L, DataReader::uint32, 0xFF, 0xFF, 0x80, 0x00);
        checkNumber(16777215L, DataReader::uint32, 0xFF, 0xFF, 0xFF, 0x00);
        checkNumber(2164260863L, DataReader::uint32, 0xFF, 0xFF, 0xFF, 0x80);
        checkNumber(4294967295L, DataReader::uint32, 0xFF, 0xFF, 0xFF, 0xFF);
    }

    @Test
    public void test64() {
        checkNumber(0L, DataReader::int64, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0);
        checkNumber(255L, DataReader::int64, 0xFF, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0);
        checkNumber(65535L, DataReader::int64, 0xFF, 0xFF, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0);
        checkNumber(16777215L, DataReader::int64, 0xFF, 0xFF, 0xFF, 0x0, 0x0, 0x0, 0x0, 0x0);
        checkNumber(4294967295L, DataReader::int64, 0xFF, 0xFF, 0xFF, 0xFF, 0x0, 0x0, 0x0, 0x0);
        checkNumber(1099511627775L, DataReader::int64, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0, 0x0, 0x0);
        checkNumber(281474976710655L, DataReader::int64, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0, 0x0);
        checkNumber(72057594037927935L, DataReader::int64, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0);
        checkNumber(9223372036854775807L, DataReader::int64, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x7F);
        checkNumber(-9151314442816847873L, DataReader::int64, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x80);
        checkNumber(-1L, DataReader::int64, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF);
    }

    @Test
    public void testString() {
        checkString("ANUS", 0x41, 0x4E, 0x55, 0x53, 0x0);
    }

    private <T extends Number> void checkNumber(final T expected, final Function<DataReader,T> getter, final int... rawData) {
        final byte[] input = new byte[rawData.length];
        for (int index = 0; index < rawData.length; index++) {
            input[index] = (byte) rawData[index];
        }
        final DataReader dataReader = new DataReader(input);
        final T actual = getter.apply(dataReader);
        Assertions.assertEquals(expected, actual);
    }

    private void checkString(final String expected, final int... rawData) {
        final byte[] input = new byte[rawData.length];
        for (int index = 0; index < rawData.length; index++) {
            input[index] = (byte) rawData[index];
        }
        final DataReader dataReader = new DataReader(input);
        final String actual = dataReader.string();
        Assertions.assertEquals(expected, actual);
    }

}
