package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.clientapp.WorldObject;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.messagebroker.ObjectDeltaType;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 19-11-2019 00:28</p>
 */
final class WorldObjectState {

    private final Map<Long, WorldObjectImpl> index = new HashMap<>();

    private ResourceState resourceState;

    WorldObjectState(final ResourceState resourceState) {
        this.resourceState = resourceState;
    }

    synchronized void receiveObjectData(final JsonNode objectDataNode) {
        final ObjectDataAccessor objectData = new ObjectDataAccessor(objectDataNode);
        final WorldObjectImpl object = index.computeIfAbsent(objectData.getId(), WorldObjectImpl::new);

        final Integer frame = objectData.getFrame();
        if (object.getFrame() >= frame) {
            return;
        }

        object.setFrame(frame);

        for (final JsonNode deltaNode : objectData.getDeltas()) {
            final ObjectDeltaAccessor delta = new ObjectDeltaAccessor(deltaNode);
            final ObjectDeltaType type = delta.getType();
            if (type == null) {
                continue;
            }

            switch (type) {
                case OD_REM:
                    index.remove(objectData.getId());
                    break;
                case OD_MOVE:
                    object.setPosition(delta.getPoint());
                    break;
                case OD_LINBEG:
                    object.setMoving(true);
                    break;
                case OD_LINSTEP:
                    object.setMoving(delta.getW() != -1);
                    break;
                case OD_RES:
                    object.setResourceId(delta.getResourceId());
                    break;
            }
        }
    }

    synchronized IntPoint getObjectPosition(final Long id) {
        return Optional.ofNullable(index.get(id))
                .map(WorldObjectImpl::getPosition)
                .orElse(null);
    }

    synchronized List<WorldObject> getWorldObjects() {
        return new ArrayList<>(index.values());
    }

    private static final class ObjectDataAccessor {
        private static final String ID = "id";
        private static final String FRAME = "frame";
        private static final String DELTAS = "deltas";

        private JsonNode data;

        ObjectDataAccessor(final JsonNode data) {
            this.data = data;
        }

        Long getId() {
            return JsonUtil.asLong(data, ID);
        }


        Integer getFrame() {
            return JsonUtil.asInt(data, FRAME);
        }

        Iterable<JsonNode> getDeltas() {
            return JsonUtil.asIterable(data, DELTAS);
        }
    }

    private static final class ObjectDeltaAccessor {
        private static final String DELTA_TYPE = "deltaType";
        private static final String X = "x";
        private static final String Y = "y";
        private static final String RESOURCE_ID = "resourceId";
        private static final String W = "w";

        private JsonNode data;

        ObjectDeltaAccessor(final JsonNode data) {
            this.data = data;
        }

        ObjectDeltaType getType() {
            return JsonUtil.asCustomFromText(data, DELTA_TYPE, ObjectDeltaType::valueOf);
        }

        IntPoint getPoint() {
            return new IntPoint(JsonUtil.asInt(data, X), JsonUtil.asInt(data, Y));
        }

        Integer getW() {
            return JsonUtil.asInt(data, W);
        }

        Long getResourceId() {
            return JsonUtil.asLong(data, RESOURCE_ID);
        }
    }

}
