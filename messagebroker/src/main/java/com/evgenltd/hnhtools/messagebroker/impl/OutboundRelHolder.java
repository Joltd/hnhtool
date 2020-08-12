package com.evgenltd.hnhtools.messagebroker.impl;

import com.evgenltd.hnhtools.util.ByteUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

final class OutboundRelHolder {

    private Integer acknowledgeSequence = 0;
    private final List<RelRequest> awaiting = new ArrayList<>();

    @UserThread
    void register(final int id, final String name, final Object... args) {
        final RelRequest relRequest = new RelRequest(id, acknowledgeSequence, name, args);
        acknowledgeSequence = ByteUtil.toShort(acknowledgeSequence + 1);
        synchronized (this) {
            awaiting.add(relRequest);
        }
        relRequest.await();
    }

    @InboundThread
    synchronized void acknowledge(final Integer sequence) {
        final Iterator<RelRequest> iterator = awaiting.iterator();
        while (iterator.hasNext()) {
            final RelRequest relRequest = iterator.next();
            if (relRequest.getSequence() <= sequence) {
                iterator.remove();
                relRequest.acknowledge();
            }
        }
    }

    @OutboundThread
    synchronized List<RelRequest> getNextAwaiting() {
        return awaiting.stream()
                .filter(RelRequest::isAttemptTimeoutExceeded)
                .peek(RelRequest::incrementAttempt)
                .collect(Collectors.toList());
    }



}
