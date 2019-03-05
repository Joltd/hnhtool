package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.util.ByteUtil;
import org.junit.jupiter.api.Test;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 05-03-2019 00:52</p>
 */
public class DataReaderTest {

    @Test
    public void test() {
        System.out.printf("%X %s %X %X\n", Byte.MAX_VALUE, Byte.MAX_VALUE, ByteUtil.unsigned(Byte.MAX_VALUE), ByteUtil.unsigned(Byte.MAX_VALUE) & 0xFF);
        System.out.printf("%X %s %X %X\n", Byte.MIN_VALUE, Byte.MIN_VALUE, ByteUtil.unsigned(Byte.MIN_VALUE), ByteUtil.unsigned(Byte.MIN_VALUE) & 0xFF);
        System.out.printf("%X %s\n", ByteUtil.BYTE, ByteUtil.BYTE);
    }

}
