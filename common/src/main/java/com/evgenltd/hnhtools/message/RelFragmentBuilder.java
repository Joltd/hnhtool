package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.common.ApplicationException;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-02-2019 00:34</p>
 */
class RelFragmentBuilder {

    private int type;
    private byte[] composition;

    boolean build(final DataReader reader) {
        final int head = reader.uint8();
        if ((head & 0x80) == 0) {
            if (composition != null) {
                throw new ApplicationException("Another fragment in build progress");
            }
            composition = reader.bytes();
            type = head;
        } else if (head == 0x80 || head == 0x81) {
            final byte[] fragment = reader.bytes();
            final byte[] newComposition = new byte[composition.length + fragment.length];
            System.arraycopy(composition, 0, newComposition, 0, composition.length);
            System.arraycopy(fragment, 0, newComposition, composition.length, fragment.length);
            composition = newComposition;
            return head == 0x81;
        } else {
            throw new ApplicationException("Unknown rel fragment type [%s]", head);
        }
        return false;
    }

    public int getType() {
        return type;
    }

    public byte[] getData() {
        return composition;
    }

    void clear() {
        type = 0;
        composition = null;
    }

}
