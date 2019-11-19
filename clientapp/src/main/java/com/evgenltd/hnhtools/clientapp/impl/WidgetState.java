package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.message.Message;
import com.evgenltd.hnhtools.message.RelType;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 19-11-2019 00:28</p>
 */
final class WidgetState {

    private final Map<Integer, WidgetImpl> index = new HashMap<>();

    private ResourceState resourceState;

    WidgetState(final ResourceState resourceState) {
        this.resourceState = resourceState;
    }

    void receiveRel(final Message.Rel rel) {
        final RelType type = rel.getRelType();
        if (type == null) {
            return;
        }

        Integer widgetId;

        switch (type) {
            case REL_MESSAGE_NEW_WIDGET:
            case REL_MESSAGE_ADD_WIDGET:
                final WidgetImpl widget = new WidgetImpl(rel);
                index.put(widget.getId(), widget);
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:
                widgetId = WidgetImpl.getId(rel.getData());

                break;
            case REL_MESSAGE_DESTROY_WIDGET:
                widgetId = WidgetImpl.getId(rel.getData());
                index.remove(widgetId);
                break;
            case REL_MESSAGE_RESOURCE_ID:
                resourceState.putResource(rel.getData());
                break;
            case REL_MESSAGE_CHARACTER_ATTRIBUTE:
                break;
        }
    }

}
