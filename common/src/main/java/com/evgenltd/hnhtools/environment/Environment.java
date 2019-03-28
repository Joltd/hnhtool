package com.evgenltd.hnhtools.environment;

import com.evgenltd.hnhtools.baseclient.BaseClient;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.DoublePoint;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.message.RelType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-03-2019 22:55</p>
 */
public class Environment {

    private static final DoublePoint MODIFIER = new DoublePoint(0x1.0p-10 * 11, 0x1.0p-10 * 11); // very magic Loftar numbers

    private final Map<Long, WorldObject> objectIndex = new HashMap<>();
    private final Map<Integer, String> resourceIndex = new HashMap<>(); // in future it should be moved to global storage
    private final Map<Integer, Widget> widgetIndex = new HashMap<>();
    private Widget mapView;
    private WorldObject player;

    private ObjectMapper objectMapper;

    public Environment(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void linkBaseClient(final BaseClient baseClient) {
        baseClient.setRelQueue(this::registerRel);
        baseClient.setObjectDataQueue(this::registerObjectData);
    }

    private synchronized void registerRel(final InboundMessageAccessor.RelAccessor relAccessor) {
        final RelType relType = relAccessor.getRelType();
        if (relType == null) {
            return;
        }

        switch (relType) {
            case REL_MESSAGE_NEW_WIDGET:
                final Widget newWidget = new Widget();
                newWidget.setId(relAccessor.getWidgetId());
                newWidget.setType(relAccessor.getWidgetType());
                newWidget.getRels().add(relAccessor);
                widgetIndex.put(newWidget.getId(), newWidget);
                if (newWidget.getType().equals("mapview")) {
                    mapView = newWidget;
                    final InboundMessageAccessor.RelArgsAccessor cArgs = relAccessor.getCArgs();
                    cArgs.next();
                    cArgs.next();
                    final Long playerObjectId = cArgs.asLong();
                    player = objectIndex.get(playerObjectId);
                }
                break;
            case REL_MESSAGE_ADD_WIDGET:
                final Widget addWidget = new Widget();
                addWidget.setId(relAccessor.getWidgetId());
                addWidget.getRels().add(relAccessor);
                widgetIndex.put(addWidget.getId(), addWidget);
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:
                final Widget widget = widgetIndex.get(relAccessor.getWidgetId());
                if (widget != null) {
                    widget.getRels().add(relAccessor);
                }
                break;
            case REL_MESSAGE_DESTROY_WIDGET:
                widgetIndex.remove(relAccessor.getWidgetId());
                break;
        }

        switch (relType) {
            case REL_MESSAGE_RESOURCE_ID:
                resourceIndex.put(relAccessor.getResourceId(), relAccessor.getResourceName());
                break;
        }

    }

    private synchronized void registerObjectData(final InboundMessageAccessor.ObjectDataAccessor accessor) {
        final Long objectId = accessor.getId();
        final WorldObject worldObject = objectIndex.computeIfAbsent(objectId, WorldObject::new);

        final int frame = accessor.getFrame();
        if (worldObject.getFrame() >= frame) {
            return;
        }

        worldObject.setFrame(frame);

        for (final InboundMessageAccessor.ObjectDataDeltaAccessor delta : accessor.getDeltas()) {
            if (delta.getType() == null) {
                continue;
            }
            switch (delta.getType()) {
                case OD_REM:
                    objectIndex.remove(objectId);
                    break;
                case OD_MOVE:
                    worldObject.setPosition(new IntPoint(delta.getX(), delta.getY()).multiple(MODIFIER));
                    break;
                case OD_RES:
                    worldObject.setResourceId(delta.getResourceId());
                    break;
            }
        }
    }

    public void store() {
        try {
            objectMapper.writeValue(new File("objectIndex.json"), objectIndex);
            objectMapper.writeValue(new File("resourceIndex.json"), resourceIndex);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public synchronized Integer getMapViewId() {
        return mapView.getId();
    }

    public synchronized IntPoint getPlayerPosition() {
        return player != null
                ? player.getPosition().devide(MODIFIER).asIntPoint()
                : null;
    }

    public Map<Long, WorldObject> getObjectIndex() {
        return objectIndex;
    }

    public Map<Integer, String> getResourceIndex() {
        return resourceIndex;
    }

    public Map<Integer, Widget> getWidgetIndex() {
        return widgetIndex;
    }
}
