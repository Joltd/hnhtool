package com.evgenltd.hnhtools.message;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 06-03-2019 01:26</p>
 */
public class DataWriterTest {

    @Test
    public void testInt8() {
        check(0x0, DataWriter::adduint8, DataReader::uint8);
        check(0x7F, DataWriter::adduint8, DataReader::uint8);
        check(0x80, DataWriter::adduint8, DataReader::uint8);
        check(0xFF, DataWriter::adduint8, DataReader::uint8);
    }

    @Test
    public void testInt16() {
        check(0x00_00, DataWriter::adduint16, DataReader::uint16);
        check(0x00_7F, DataWriter::adduint16, DataReader::uint16);
        check(0x00_80, DataWriter::adduint16, DataReader::uint16);
        check(0x00_FF, DataWriter::adduint16, DataReader::uint16);
        check(0x7F_00, DataWriter::adduint16, DataReader::uint16);
        check(0x80_00, DataWriter::adduint16, DataReader::uint16);
        check(0xCC_CC, DataWriter::adduint16, DataReader::uint16);
        check(0xFF_FF, DataWriter::adduint16, DataReader::uint16);
    }

    @Test
    public void testInt32() {
        check(0x00_00_00_00L, DataWriter::adduint32, DataReader::uint32);
        check(0x00_00_00_FFL, DataWriter::adduint32, DataReader::uint32);
        check(0x00_00_FF_FFL, DataWriter::adduint32, DataReader::uint32);
        check(0x00_FF_FF_FFL, DataWriter::adduint32, DataReader::uint32);
        check(0xFF_FF_FF_FFL, DataWriter::adduint32, DataReader::uint32);

        check(0x00_00_00_00, DataWriter::addint32, DataReader::int32);
        check(0x00_00_00_FF, DataWriter::addint32, DataReader::int32);
        check(0x00_00_FF_FF, DataWriter::addint32, DataReader::int32);
        check(0x00_FF_FF_FF, DataWriter::addint32, DataReader::int32);
        check(0xFF_FF_FF_FF, DataWriter::addint32, DataReader::int32);
    }

    @Test
    public void testInt64() {
        check(0x00_00_00_00_00_00_00_00L, DataWriter::addint64, DataReader::int64);
        check(0x00_00_00_00_00_00_00_FFL, DataWriter::addint64, DataReader::int64);
        check(0x00_00_00_00_00_00_FF_FFL, DataWriter::addint64, DataReader::int64);
        check(0x00_00_00_00_00_FF_FF_FFL, DataWriter::addint64, DataReader::int64);
        check(0x00_00_00_00_FF_FF_FF_FFL, DataWriter::addint64, DataReader::int64);
        check(0x00_00_00_FF_FF_FF_FF_FFL, DataWriter::addint64, DataReader::int64);
        check(0x00_00_FF_FF_FF_FF_FF_FFL, DataWriter::addint64, DataReader::int64);
        check(0x00_FF_FF_FF_FF_FF_FF_FFL, DataWriter::addint64, DataReader::int64);
        check(0xFF_FF_FF_FF_FF_FF_FF_FFL, DataWriter::addint64, DataReader::int64);
    }

    @Test
    public void test() {
        System.out.printf("%08X\n", 0xFF_FF_FF_FF >> 8);
        System.out.printf("%08X\n", 0xFF_FF_FF_FF >> 16);
        System.out.printf("%08X\n", 0xFF_FF_FF_FF >> 24);
        System.out.printf("%08X\n", 0xFF_FF_FF_FF >> 32);
    }

    private <T extends Number> void check(final T expected, final BiConsumer<DataWriter,T> setter, final Function<DataReader,T> getter) {
        final DataWriter dataWriter = new DataWriter();
        setter.accept(dataWriter, expected);
        final byte[] result = dataWriter.bytes();
        final DataReader dataReader = new DataReader(result);
        final T actual = getter.apply(dataReader);
        Assertions.assertEquals(expected, actual);
    }

}
