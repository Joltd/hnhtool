package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.message.RelType;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 23:00</p>
 */
class RelMessageHandler {

    private ComplexClient client;

    RelMessageHandler(final ComplexClient client) {
        this.client = client;
    }

    void handleRelMessage(final InboundMessageAccessor.RelAccessor accessor) {
        final RelType type = accessor.getRelType();
        if (type == null) {
            return;
        }

        final int widgetId = accessor.getWidgetId();
        switch (type) {
            case REL_MESSAGE_NEW_WIDGET:
                client.widgetIndex.addWidget(widgetId, accessor.getWidgetType());
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:

                break;
            case REL_MESSAGE_DESTROY_WIDGET:
                client.widgetIndex.removeWidget(widgetId);
                break;
            case REL_MESSAGE_ADD_WIDGET:
                client.widgetIndex.addWidget(widgetId, null);
                break;
            case REL_MESSAGE_RESOURCE_ID:
                client.resourceProvider.saveResource(accessor.getResourceId(), accessor.getResourceName());
                break;
            case REL_MESSAGE_CHARACTER_ATTRIBUTE:
                break;
        }
    }

    private void handleNewWidget() {

    }



    private void handleDestroyWidget() {

    }

}
