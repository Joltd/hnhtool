package com.evgenltd.hnhtools.baseclient;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 09-03-2019 16:29</p>
 */
final class OutboundMessage {

    private long sendTime = 0;
    private final byte[] data;

    public OutboundMessage(final byte[] data) {
        this.data = data;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(final long sendTime) {
        this.sendTime = sendTime;
    }

    public byte[] getData() {
        return data;
    }

}
