package com.evgenltd.hnhtools.messagebroker.impl;

import com.evgenltd.hnhtools.util.ByteUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 12-03-2019 23:27</p>
 */
final class OutboundRelHolder {

    private Integer acknowledgeSequence = 0;
    private List<RelRequest> awaiting = new ArrayList<>();

    synchronized CompletableFuture<Void> register(final int id, final String name, final Object... args) {
        final RelRequest relRequest = new RelRequest(id, acknowledgeSequence, name, args);
        acknowledgeSequence = ByteUtil.toShort(acknowledgeSequence + 1);
        awaiting.add(relRequest);
        return relRequest.getFuture();
    }

    synchronized void acknowledge(final Integer sequence) {
        final Iterator<RelRequest> iterator = awaiting.iterator();
        while (iterator.hasNext()) {
            final RelRequest relRequest = iterator.next();
            if (relRequest.getSequence() <= sequence) {
                iterator.remove();
                relRequest.getFuture().complete(null);
            }
        }
    }

    synchronized List<RelRequest> getNextAwaiting() {
        return awaiting.stream()
                .filter(RelRequest::isAttemptTimeoutExceeded)
                .peek(RelRequest::incrementAttempt)
                .collect(Collectors.toList());
    }



}
