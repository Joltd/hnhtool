package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.complexclient.entity.impl.WorldObjectImpl;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.message.InboundMessageAccessor;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 23:00</p>
 */
final class ObjectDataHandler {

    private ComplexClient client;

    ObjectDataHandler(final ComplexClient client) {
        this.client = client;
    }

    void handleObjectData(final InboundMessageAccessor.ObjectDataAccessor accessor) {
        final Long objectId = accessor.getId();
        final WorldObjectImpl object = client.getObjectIndex().getObject(objectId);

        final int frame = accessor.getFrame();
        if (object.getFrame() >= frame) {
            return;
        }

        object.setFrame(frame);

        for (final InboundMessageAccessor.ObjectDataDeltaAccessor delta : accessor.getDeltas()) {
            if (delta.getType() == null) {
                continue;
            }
            switch (delta.getType()) {
                case OD_REM:
                    client.getObjectIndex().removeObject(objectId);
                    break;
                case OD_MOVE:
                    object.setPosition(new IntPoint(delta.getX(), delta.getY()));
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

}
