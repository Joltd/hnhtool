package com.evgenltd.hnhtools.baseclient;

import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.util.ByteUtil;

import java.util.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 10-03-2019 15:57</p>
 */
final class StateHolder {

    private Integer acknowledgeRelSequence = null;
    private Integer expectedRelSequence = 0;
    private Map<Integer, InboundMessageAccessor.RelAccessor> awaitingRel = new HashMap<>();

    private Map<Long,ObjectDataEntry> objectDataAcknowledge = new HashMap<>();

    // ##################################################
    // #                                                #
    // #  Rel                                           #
    // #                                                #
    // ##################################################

    /**
     * <p>Passed REL will be registered if REL sequence number:
     * <ul>
     *     <li>matches with expected sequence number</li>
     *     <li>is a number from the future (but no more than half-WORD</li>
     * </ul></p>
     * <p>In first case REL is an actual which should be handled (pushed to a queue) immediately</p>
     * <p>In second case REL is an portion of data which received too early, so we should to store it
     * and handle when expected sequence number will matches with it</p>
     * @param data target REL
     * @return passed REL if its sequence number matches with expected sequence number, <code>null</code> otherwise
     */
    InboundMessageAccessor.RelAccessor registerRel(final InboundMessageAccessor.RelAccessor data) {
        final Integer relSequence = data.getSequence();
        final boolean isFutureRel = ByteUtil.toShort(relSequence - expectedRelSequence) < ByteUtil.WORD / 2;
        if (isFutureRel) {
            awaitingRel.put(relSequence, data);
            return null;
        }

        if (Objects.equals(relSequence, expectedRelSequence)) {
            incrementExpectedRelSequence();
            return data;
        }

        return null;
    }

    @UsedInInboundThread
    private synchronized void incrementExpectedRelSequence() {
        acknowledgeRelSequence = expectedRelSequence;
        expectedRelSequence = ByteUtil.toShort(expectedRelSequence + 1);
    }

    List<InboundMessageAccessor.RelAccessor> getNearestAwaitingRel() {
        final List<InboundMessageAccessor.RelAccessor> nextRelList = new ArrayList<>();
        final boolean hasNextRel = awaitingRel.containsKey(expectedRelSequence);
        if (hasNextRel) {
            final InboundMessageAccessor.RelAccessor nextRel = awaitingRel.remove(expectedRelSequence);
            nextRelList.add(nextRel);
            incrementExpectedRelSequence();
        }
        return nextRelList;
    }

    @UsedInOutboundThread
    synchronized Integer getRelSequenceForAcknowledge() {
        if (acknowledgeRelSequence == null) {
            return null;
        }
        final Integer forAcknowledge = acknowledgeRelSequence;
        acknowledgeRelSequence = null;
        return forAcknowledge;
    }

    // ##################################################
    // #                                                #
    // #  Object data                                   #
    // #                                                #
    // ##################################################

    @UsedInInboundThread
    synchronized void registerObjectData(final InboundMessageAccessor.ObjectDataAccessor objectDataAccessor) {
        final long objectId = objectDataAccessor.getId();
        final ObjectDataEntry objectData = objectDataAcknowledge.getOrDefault(objectId, new ObjectDataEntry());
        objectDataAcknowledge.put(objectId, objectData);
        objectData.setId(objectId);
        objectData.setFrame(objectDataAccessor.getFrame());
        objectData.setReceivedTime(System.currentTimeMillis());
    }

    @UsedInOutboundThread
    synchronized List<ObjectDataEntry> getObjectDataForAcknowledge() {
        final List<ObjectDataEntry> result = new ArrayList<>();
        for (Long objectId : objectDataAcknowledge.keySet()) {
            final ObjectDataEntry objectData = objectDataAcknowledge.remove(objectId);
            result.add(objectData);
        }
        return result;
    }

    static final class ObjectDataEntry {

        private long id;
        private int frame;
        private long receivedTime;

        public long getId() {
            return id;
        }

        public void setId(final long id) {
            this.id = id;
        }

        public int getFrame() {
            return frame;
        }

        public void setFrame(final int frame) {
            this.frame = frame;
        }

        public long getReceivedTime() {
            return receivedTime;
        }

        public void setReceivedTime(final long receivedTime) {
            this.receivedTime = receivedTime;
        }
    }

}
