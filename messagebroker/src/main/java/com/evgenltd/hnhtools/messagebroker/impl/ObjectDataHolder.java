package com.evgenltd.hnhtools.messagebroker.impl;

import com.evgenltd.hnhtools.message.InboundMessageAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 10-03-2019 15:57</p>
 */
final class ObjectDataHolder {

    private Map<Long,ObjectDataEntry> objectDataAcknowledge = new HashMap<>();

    @InboundThread
    synchronized void registerObjectData(final InboundMessageAccessor.ObjectDataAccessor objectDataAccessor) {
        final long objectId = objectDataAccessor.getId();
        final ObjectDataEntry objectData = objectDataAcknowledge.getOrDefault(objectId, new ObjectDataEntry());
        objectDataAcknowledge.put(objectId, objectData);
        objectData.setId(objectId);
        objectData.setFrame(objectDataAccessor.getFrame());
        objectData.setReceivedTime(System.currentTimeMillis());
    }

    @OutboundThread
    synchronized List<ObjectDataEntry> getObjectDataForAcknowledge() {
        final List<ObjectDataEntry> result = new ArrayList<>(objectDataAcknowledge.values());
        objectDataAcknowledge.clear();
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

        int getFrame() {
            return frame;
        }

        void setFrame(final int frame) {
            this.frame = frame;
        }

        long getReceivedTime() {
            return receivedTime;
        }

        void setReceivedTime(final long receivedTime) {
            this.receivedTime = receivedTime;
        }
    }

}
