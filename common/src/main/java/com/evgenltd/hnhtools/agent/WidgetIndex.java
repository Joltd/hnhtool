package com.evgenltd.hnhtools.agent;

import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.message.RelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 13:53</p>
 */
final class WidgetIndex {

    private static final String MAP_VIEW_WIDGET = "mapview";
    private static final String EQUIP_WIDGET = "qpry";
    private static final String INVENTORY_WIDGET = "inv";
    private static final String ITEM_WIDGET = "item";
    private static final String ISBOX_WIDGET = "isbox";
    private static final String CONTEXT_MENU = "sm";

    private static final String LABEL_MESSAGE_NAME = "tt";
    private static final String CHANGE_NUM_MESSAGE_NAME = "chnum";

    private final Map<Integer, Widget> index = new HashMap<>();
    private final Map<Integer, Item> itemIndex = new HashMap<>();
    private final Map<Integer, Stack> stackIndex = new HashMap<>();

    private Long characterObjectId;
    private Widget mapView;
    private Widget equip;
    private Widget characterInventory;
    private Widget lastOpenedInventory;
    private ContextMenu contextMenu;

    // ##################################################
    // #                                                #
    // #  API                                           #
    // #                                                #
    // ##################################################

    synchronized void printState() {
        System.out.println("Common index");
        index.forEach((id,widget) -> System.out.println(String.format("id=[%s], type=[%s]", id, widget.getType())));

        System.out.println("Item index");
        itemIndex.forEach((id,item) -> System.out.println(String.format("id=[%s], parentId=[%s]", id, item.getParentId())));

        System.out.println("Stack index");
        stackIndex.forEach((id,stack) -> System.out.println(String.format("id=[%s], count=[%s/%s]", id, stack.getCount(), stack.getMax())));
    }

    @Nullable
    synchronized Long getCharacterObjectId() {
        return characterObjectId;
    }

    @Nullable
    synchronized Integer getMapViewId() {
        return mapView != null ? mapView.getId() : null;
    }

    @Nullable
    synchronized Integer getCharacterInventoryId() {
        return characterInventory != null ? characterInventory.getId() : null;
    }

    @Nullable
    synchronized Integer getCharacterEquipId() {
        return equip != null ? equip.getId() : null;
    }

    @Nullable
    synchronized Integer getLastOpenedInventory() {
        return lastOpenedInventory != null ? lastOpenedInventory.getId() : null;
    }

    @NotNull
    synchronized List<Item> getInventoryItems(final Integer inventoryId) {
        return itemIndex.values()
                .stream()
                .filter(item -> item.getParentId().equals(inventoryId))
                .collect(Collectors.toList());
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

    // ##################################################
    // #                                                #
    // #  Handlers                                      #
    // #                                                #
    // ##################################################

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
                if (lastOpenedInventory != null && lastOpenedInventory.getId().equals(widgetId)) {
                    lastOpenedInventory = null;
                }
                if (contextMenu != null && contextMenu.getId().equals(widgetId)) {
                    contextMenu = null;
                }
                break;
        }
    }

    private void handleNewSpecialWidgets(final Widget widget, final InboundMessageAccessor.RelAccessor accessor) {
        final String type = widget.getType();
        switch (type) {
            case MAP_VIEW_WIDGET:
                mapView = widget;
                final InboundMessageAccessor.RelArgsAccessor mapViewArgs = accessor.getCArgs();
                mapViewArgs.skip();
                mapViewArgs.skip();
                characterObjectId = mapViewArgs.nextLong();
                break;
            case CONTEXT_MENU:
                contextMenu = new ContextMenu(widget.getId());
                final InboundMessageAccessor.RelArgsAccessor contextMenuArgs = accessor.getCArgs();
                contextMenu.getCommands().add(contextMenuArgs.nextString());
                break;
            case EQUIP_WIDGET:
                equip = widget;
                break;
            case INVENTORY_WIDGET:
                final Integer inventoryParentId = accessor.getWidgetParentId();
                if (inventoryParentId.equals(mapView.getId())) {
                    characterInventory = widget;
                } else {
                    lastOpenedInventory = widget;
                }
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
            case CHANGE_NUM_MESSAGE_NAME:
                final Stack stack = stackIndex.get(widget.getId());
                if (stack != null) {
                    final InboundMessageAccessor.RelArgsAccessor stackArgs = accessor.getArgs();
                    stack.setCount(stackArgs.nextInt());
                    stack.setMax(stackArgs.nextInt());
                }
                break;
        }
    }

    // ##################################################
    // #                                                #
    // #  Inner state                                   #
    // #                                                #
    // ##################################################

    static final class Item {

        private Integer id;
        private Integer parentId;
        private String name;
        private float quality;

        Item(final Integer id, final Integer parentId) {
            this.id = id;
            this.parentId = parentId;
        }

        Integer getId() {
            return id;
        }

        Integer getParentId() {
            return parentId;
        }
    }

    private static final class Widget {

        private Integer id;
        private String type;

        // add widget message
        Widget(final Integer id) {
            this.id = id;
        }

        // new widget message
        Widget(final Integer id, final String type) {
            this.id = id;
            this.type = type;
        }

        public Integer getId() {
            return id;
        }

        public String getType() {
            return type;
        }

    }

    private static final class Stack {

        private Integer id;
        private Integer count = 0;
        private Integer max = 0;

        Stack(final Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        Integer getCount() {
            return count;
        }
        void setCount(final Integer count) {
            this.count = count;
        }

        Integer getMax() {
            return max;
        }
        void setMax(final Integer max) {
            this.max = max;
        }
    }

    private static final class ContextMenu {

        private Integer id;
        private List<String> commands = new ArrayList<>();

        ContextMenu(final Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        List<String> getCommands() {
            return commands;
        }

        int getCommandId(final String command) {
            return commands.indexOf(command);
        }

    }
}