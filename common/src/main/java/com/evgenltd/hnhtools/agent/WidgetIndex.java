package com.evgenltd.hnhtools.agent;

import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.message.RelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 13:53</p>
 */
final class WidgetIndex {

    private static final String MAPVIEW_WIDGET = "mapview";
    private static final String ITEM_WIDGET = "item";
    private static final String ISBOX_WIDGET = "isbox";
    private static final String COTNEXT_MENU = "sm";

    private static final String LABEL_MESSAGE_NAME = "tt";
    private static final String CHNUM_MESSAGE_NAME = "chnum";

    private final Map<Integer, Widget> index = new HashMap<>();
    private final Map<Integer, Item> itemIndex = new HashMap<>();
    private final Map<Integer, Stack> stackIndex = new HashMap<>();

    private Long characterObjectId;
    private Widget mapView;
    private ContextMenu contextMenu;

    @Nullable
    synchronized Long getCharacterObjectId() {
        return characterObjectId;
    }

    @Nullable
    synchronized Integer getMapViewId() {
        return mapView != null ? mapView.getId() : null;
    }

    @Nullable
    synchronized Item getItem(final Integer itemId) {
        return itemIndex.get(itemId);
    }

    @Nullable
    synchronized Integer getContextMenuId() {
        return contextMenu != null ? contextMenu.getId() : null;
    }

    @NotNull
    synchronized Integer getContextMenuCommandId(final String command) {
        if (contextMenu == null) {
            return -1;
        }

        return contextMenu.getCommandId(command);
    }

    synchronized void registerWidgetMessage(
            final RelType type,
            final InboundMessageAccessor.RelAccessor accessor
    ) {
        final int widgetId = accessor.getWidgetId();
        switch (type) {
            case REL_MESSAGE_NEW_WIDGET:
                final Widget newWidget = new Widget(
                        widgetId,
                        accessor.getWidgetType()
                );
                index.put(newWidget.getId(), newWidget);
                handleNewSpecialWidgets(newWidget, accessor);
                break;
            case REL_MESSAGE_ADD_WIDGET:
                final Widget addWidget = new Widget(widgetId);
                index.put(addWidget.getId(), addWidget);
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:
                final Widget widget = index.get(widgetId);
                if (widget != null) {
                    handleSpecialWidgetMessages(widget, accessor);
                }
                break;
            case REL_MESSAGE_DESTROY_WIDGET:
                index.remove(widgetId);
                itemIndex.remove(widgetId);
                stackIndex.remove(widgetId);
                if (contextMenu != null && contextMenu.getId().equals(widgetId)) {
                    contextMenu = null;
                }
                break;
        }
    }

    private void handleNewSpecialWidgets(final Widget widget, final InboundMessageAccessor.RelAccessor accessor) {
        final String type = widget.getType();
        switch (type) {
            case MAPVIEW_WIDGET:
                mapView = widget;
                final InboundMessageAccessor.RelArgsAccessor mapViewArgs = accessor.getCArgs();
                mapViewArgs.skip();
                mapViewArgs.skip();
                characterObjectId = mapViewArgs.nextLong();
                break;
            case COTNEXT_MENU:
                contextMenu = new ContextMenu(widget.getId());
                final InboundMessageAccessor.RelArgsAccessor contextMenuArgs = accessor.getCArgs();
                contextMenu.getCommands().add(contextMenuArgs.nextString());
                break;
            case ITEM_WIDGET:
                final int parentId = accessor.getWidgetParentId();
                if (index.containsKey(parentId)) {
                    final Item item = new Item(widget.getId(), parentId);
                    itemIndex.put(widget.getId(), item);
                }
                break;
            case ISBOX_WIDGET:
                final Stack stack = new Stack(widget.getId());
                final InboundMessageAccessor.RelArgsAccessor stackArgs = accessor.getCArgs();
                stackArgs.skip();
                stack.setCount(stackArgs.nextInt());
                stack.setMax(stackArgs.nextInt());
                stackIndex.put(widget.getId(), stack);
                break;
        }
    }

    private void handleSpecialWidgetMessages(final Widget widget, final InboundMessageAccessor.RelAccessor accessor) {
        final String messageName = accessor.getWidgetMessageName();
        switch (messageName) {
            case LABEL_MESSAGE_NAME:
                final Item item = itemIndex.get(widget.getId());
//                if (item != null) {
//                    // todo fill item attributes
//                }
                break;
            case CHNUM_MESSAGE_NAME:
                final Stack stack = stackIndex.get(widget.getId());
                if (stack != null) {
                    final InboundMessageAccessor.RelArgsAccessor stackArgs = accessor.getArgs();
                    stack.setCount(stackArgs.nextInt());
                    stack.setMax(stackArgs.nextInt());
                }
                break;
        }
    }

}