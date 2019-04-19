package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.entity.Character;
import com.evgenltd.hnhtools.entity.Inventory;
import com.evgenltd.hnhtools.entity.WorldItem;
import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.message.RelType;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 23:00</p>
 */
class RelMessageHandler {

    private static final String GAME_UI_WIDGET = "gameui";
    private static final String MAP_VIEW_WIDGET = "mapview";
    private static final String EQUIP_WIDGET = "epry";
    private static final String INVENTORY_WIDGET = "inv";
    private static final String ITEM_WIDGET = "item";
    private static final String CHARACTER_SHEET_WIDGET = "chr";
    private static final String SPEED_WIDGET = "speedget";
    private static final String CRAFT_MENU_WIDGET = "scm";
    private static final String ISBOX_WIDGET = "isbox";
    private static final String CONTEXT_MENU = "sm";

    private static final String LABEL_MESSAGE_NAME = "tt";
    private static final String CHANGE_NUM_MESSAGE_NAME = "chnum";

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
                final Widget newWidget = client.getWidgetIndex().addWidget(widgetId, accessor.getWidgetType());
                handleNewWidget(newWidget, accessor);
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:
                final Widget widget = client.getWidgetIndex().getWidget(widgetId);
                if (widget != null) {
                    widget.handleMessage(accessor);
                }
                break;
            case REL_MESSAGE_DESTROY_WIDGET:
                final Widget destroyWidget = client.getWidgetIndex().removeWidget(widgetId);
                if (destroyWidget != null) {
                    destroyWidget.destroy();
                }
                break;
            case REL_MESSAGE_ADD_WIDGET:
                client.getWidgetIndex().addWidget(widgetId, null);
                break;
            case REL_MESSAGE_RESOURCE_ID:
                client.getResourceProvider().saveResource(accessor.getResourceId(), accessor.getResourceName());
                break;
            case REL_MESSAGE_CHARACTER_ATTRIBUTE:
                break;
        }
    }

    private void handleNewWidget(final Widget widget, final InboundMessageAccessor.RelAccessor accessor) {
        final String type = widget.getType();
        final Character character = client.getCharacter_();
        switch (type) {
            case GAME_UI_WIDGET:
                client.setGameUiId(widget.getId());
                widget.setDestroy(() -> client.setGameUiId(null));
                break;
            case MAP_VIEW_WIDGET:
                client.setMapViewId(widget.getId());
                final InboundMessageAccessor.RelArgsAccessor mapViewArgs = accessor.getCArgs();
                mapViewArgs.skip();
                mapViewArgs.skip();
                character.setId(mapViewArgs.nextLong());
                widget.setDestroy(() -> client.setMapViewId(null));
                break;
            case EQUIP_WIDGET:
                final Inventory equip = new Inventory(widget.getId());
                character.setEquip(equip);
                widget.setDestroy(() -> character.setEquip(null));
                break;
            case CHARACTER_SHEET_WIDGET:
                character.setSheetId(widget.getId());
                widget.setDestroy(() -> character.setSheetId(null));
                break;
            case SPEED_WIDGET:
                character.setSpeedId(widget.getId());
                widget.setDestroy(() -> character.setSpeedId(null));
                break;
            case CRAFT_MENU_WIDGET:
                client.setCraftMenuId(widget.getId());
                widget.setDestroy(() -> client.setCraftMenuId(null));
                break;
            case INVENTORY_WIDGET:
                final int inventoryParentId = accessor.getWidgetParentId();
                if (!client.getWidgetIndex().hasWidget(inventoryParentId)) {
                    break;
                }

                final Inventory inventory = new Inventory(widget.getId());

                if (client.getGameUiId().equals(inventoryParentId)) {
                    character.setMain(inventory);
                    widget.setDestroy(() -> character.setMain(null));
                } else if (character.getSheetId().equals(inventoryParentId)) {
                    character.setStudy(inventory);
                    widget.setDestroy(() -> character.setStudy(null));
                } else {
                    client.getInventories().put(inventory.getId(), inventory);
                    widget.setDestroy(() -> client.getInventories().remove(inventory.getId()));
                }

                break;
            case ITEM_WIDGET:
                final int itemParentId = accessor.getWidgetParentId();
                if (!client.getWidgetIndex().hasWidget(itemParentId)) {
                    break;
                }

                final WorldItem worldItem = new WorldItem(widget.getId());
                worldItem.setResourceId(accessor.getCArgs().nextLong());

                if (Objects.equals(itemParentId, character.getEquip().getId())) {
                    worldItem.setNumber(accessor.getPArgs().nextInt());
                } else {
                    worldItem.setPosition(accessor.getPArgs().nextPoint());
                }

//                accessor.getArgsAsString() // all info

                addToInventoryIfPossible(widget, character::getEquip, itemParentId, worldItem);
                addToInventoryIfPossible(widget, character::getStudy, itemParentId, worldItem);
                addToInventoryIfPossible(widget, character::getMain, itemParentId, worldItem);
                addToInventoryIfPossible(widget, () -> client.getInventories().get(itemParentId), itemParentId, worldItem);

                if (Objects.equals(client.getGameUiId(), itemParentId)) {
                    // in hand
                }

                break;
            case ISBOX_WIDGET:
//                final Stack stack = new Stack(widget.getId());
//                final InboundMessageAccessor.RelArgsAccessor stackArgs = accessor.getCArgs();
//                stackArgs.skip();
//                stack.setCount(stackArgs.nextInt());
//                stack.setMax(stackArgs.nextInt());
//                stackIndex.put(widget.getId(), stack);
                break;
            case CONTEXT_MENU:
//                contextMenu = new ContextMenu(widget.getId());
//                final InboundMessageAccessor.RelArgsAccessor contextMenuArgs = accessor.getCArgs();
//                contextMenu.getCommands().add(contextMenuArgs.nextString());
                break;
        }
    }

    private void addToInventoryIfPossible(final Widget widget, final Supplier<Inventory> getInventory, final Integer parentId, final WorldItem worldItem) {
        final Inventory inventory = getInventory.get();
        if (inventory == null || !Objects.equals(inventory.getId(), parentId)) {
            return;
        }

        inventory.getItems().add(worldItem);
        widget.setHandleMessage(rel -> handleItemMessage(rel, worldItem));
        widget.setDestroy(() -> {
            final Inventory inv = getInventory.get();
            if (inv != null) {
                inv.removeItem(worldItem);
            }
        });

    }

    private void handleItemMessage(final InboundMessageAccessor.RelAccessor accessor, final WorldItem worldItem) {
        if (!Objects.equals(accessor.getWidgetMessageName(), LABEL_MESSAGE_NAME)) {
            return;
        }

        // add info
    }

    private void handleStackMessage() {
//        final Stack stack = stackIndex.get(widget.getId());
//        if (stack != null) {
//            final InboundMessageAccessor.RelArgsAccessor stackArgs = accessor.getArgs();
//            stack.setCount(stackArgs.nextInt());
//            stack.setMax(stackArgs.nextInt());
//        }
    }

}
