package com.evgenltd.hnhtools.messagebroker.impl;

import com.evgenltd.hnhtools.common.ExecutionException;
import com.evgenltd.hnhtools.common.TimeoutException;
import com.evgenltd.hnhtools.message.DataWriter;
import com.evgenltd.hnhtools.messagebroker.RelType;
import com.evgenltd.hnhtools.messagebroker.impl.message.MessageType;
import com.evgenltd.hnhtools.util.ByteUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 12-03-2019 23:14</p>
 */
final class RelRequest {

    private static final long TIMEOUT = 1000;

    private int id;
    private int sequence;
    private String name;
    private Object[] args;
    private long lastAttemptTime = System.currentTimeMillis();
    private int attemptCount = 0;
    private CountDownLatch latch = new CountDownLatch(1);

    RelRequest(final int id, final int sequence, final String name, final Object[] args) {
        this.id = id;
        this.sequence = sequence;
        this.name = name;
        this.args = args;
    }

    int getSequence() {
        return sequence;
    }

    void await() {
        try {
            final boolean done = latch.await(TIMEOUT, TimeUnit.MILLISECONDS);
            if (done) {
                return;
            }
            throw new TimeoutException();
        } catch (final InterruptedException e) {
            throw new ExecutionException(e);
        }
    }

    void acknowledge() {
        latch.countDown();
    }

    DataWriter toWriter() {
        final DataWriter writer = new DataWriter();
        writer.adduint8(MessageType.MESSAGE_TYPE_REL.getValue());
        writer.adduint16(sequence);
        writer.adduint8(RelType.REL_MESSAGE_WIDGET_MESSAGE.getValue());
        writer.adduint16(id);
        writer.addString(name);
        ByteUtil.writeList(writer, args);
        return writer;
    }

    boolean isAttemptTimeoutExceeded() {
        final long timeElapsed = System.currentTimeMillis() - lastAttemptTime;
        return (attemptCount == 0 && timeElapsed > 0)
                || (attemptCount == 1 && timeElapsed > 80)
                || (attemptCount < 4 && timeElapsed > 200)
                || (attemptCount < 10 && timeElapsed > 620)
                || timeElapsed > 2000;
    }

    void incrementAttempt() {
        attemptCount++;
    }

}
