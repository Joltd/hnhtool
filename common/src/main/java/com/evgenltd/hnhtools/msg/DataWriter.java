package com.evgenltd.hnhtools.msg;

import com.evgenltd.hnhtools.util.ByteUtil;

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


    public void addint8() {

    }

    public void adduint8() {

    }

    public void addint16() {

    }

    public void adduint16(int value) {
//        data[pointer] = signed(value & 0xff);
//        data[pointer + 1] = signed((value & 0xff00) >> 8);
//        pointer = pointer + 2;
    }

    public void addint32() {

    }

    public void adduint32() {

    }

    public void addint64() {

    }

    public void addfloat32() {

    }

    public void addfloat64() {

    }

    public void addString() {

    }

    public void addbytes() {

    }

}
