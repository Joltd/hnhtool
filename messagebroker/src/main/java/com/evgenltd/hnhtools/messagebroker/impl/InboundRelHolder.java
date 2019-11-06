package com.evgenltd.hnhtools.messagebroker.impl;

import com.evgenltd.hnhtools.message.Message;
import com.evgenltd.hnhtools.util.ByteUtil;

import java.util.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 12-03-2019 23:20</p>
 */
final class InboundRelHolder {

    private Integer acknowledgeSequence = null;
    private Integer nextSequence = 0;
    private Map<Integer, Message.Rel> awaiting = new HashMap<>();

    /**
     * <p>Passed REL will be registered if REL sequence number:
     * <ul>
     *     <li>matches with expected sequence number</li>
     *     <li>is a number from the future (but no more than half-WORD</li>
     * </ul></p>
     * <p>In first case REL is an actual which should be handled (pushed to a queue) immediately</p>
     * <p>In second case REL is an portion ok data which received too early, so we should to store it
     * and handle when expected sequence number will matches with it</p>
     * @param data target REL
     * @return passed REL if its sequence number matches with expected sequence number, <code>null</code> otherwise
     */
    @InboundThread
    synchronized Message.Rel register(final Message.Rel data) {
        final Integer relSequence = data.getSequence();
        if (Objects.equals(relSequence, nextSequence)) {
            incrementNextSequence();
            return data;
        }

        final boolean isFutureRel = ByteUtil.toShort(relSequence - nextSequence) < ByteUtil.WORD / 2;
        if (isFutureRel) {
            awaiting.put(relSequence, data);
        }

        return null;
    }

    @InboundThread
    private void incrementNextSequence() {
        acknowledgeSequence = nextSequence;
        nextSequence = ByteUtil.toShort(nextSequence + 1);
    }

    @InboundThread
    synchronized List<Message.Rel> getNearestAwaiting() {
        final List<Message.Rel> nextRelList = new ArrayList<>();
        final boolean hasNextRel = awaiting.containsKey(nextSequence);
        if (hasNextRel) {
            final Message.Rel nextRel = awaiting.remove(nextSequence);
            nextRelList.add(nextRel);
            incrementNextSequence();
        }
        return nextRelList;
    }

    @OutboundThread
    synchronized Integer getAcknowledgeSequence() {
        if (acknowledgeSequence == null) {
            return null;
        }
        final Integer forAcknowledge = acknowledgeSequence;
        acknowledgeSequence = null;
        return forAcknowledge;
    }

}
