package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.complexclient.entity.ContextMenu;
import com.evgenltd.hnhtools.complexclient.entity.impl.*;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.message.RelType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 23:00</p>
 */
final class RelMessageHandler {

    private static final Logger log = LogManager.getLogger(RelMessageHandler.class);

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
    private static final String SZ_MESSAGE_NAME = "sz";

    private ComplexClient client;

    RelMessageHandler(final ComplexClient client) {
        this.client = client;
    }

    void handleRelMessage(final InboundMessageAccessor.RelAccessor accessor) {
        final RelType type = accessor.getRelType();
        if (type == null) {
            return;
        }

        switch (type) {
            case REL_MESSAGE_NEW_WIDGET:
                final Widget newWidget = client.getWidgetIndex().addWidget(accessor.getWidgetId(), accessor.getWidgetType());
                handleNewWidget(newWidget, accessor);
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:
                final Widget widget = client.getWidgetIndex().getWidget(accessor.getWidgetId());
                if (widget != null) {
                    widget.handleMessage(accessor);
                }
                break;
            case REL_MESSAGE_DESTROY_WIDGET:
                final Widget destroyWidget = client.getWidgetIndex().removeWidget(accessor.getWidgetId());
                if (destroyWidget != null) {
                    destroyWidget.destroy();
                }
                break;
            case REL_MESSAGE_ADD_WIDGET:
                client.getWidgetIndex().addWidget(accessor.getWidgetId(), null);
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
        final CharacterImpl character = client.getCharacter();
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
                final WorldInventoryImpl equip = new WorldInventoryImpl(widget.getId());
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
                if (client.getWidgetIndex().isWidgetNotPresented(inventoryParentId)) {
                    break;
                }

                final WorldInventoryImpl inventory = new WorldInventoryImpl(widget.getId());
                final IntPoint size = accessor.getCArgs().nextPoint();
                inventory.setSize(size);

                if (client.getGameUiId().equals(inventoryParentId)) {
                    inventory.setParentId(character.getId());
                    character.setMain(inventory);
                    client.getInventoryIndex().addInventory(inventory);
                    widget.setDestroy(() -> character.setMain(null));
                } else if (character.getSheetId().equals(inventoryParentId)) {
                    character.setStudy(inventory);
                    widget.setDestroy(() -> character.setStudy(null));
                } else {
                    final Number objectParentId = client.takeParentIdForNewInventory();
                    if (objectParentId == null) {
                        log.warn("Parent for Inventory [{}] is not provided, skipped", widget.getId());
                        break;
                    }
                    inventory.setParentId(objectParentId);
                    inventory.setWindowId(accessor.getWidgetParentId());
                    client.getInventoryIndex().addInventory(inventory);
                    widget.setDestroy(() -> client.getInventoryIndex().removeInventory(inventory.getId()));
                }

                widget.setHandleMessage(rel -> handleInventoryMessage(rel, inventory));

                break;
            case ITEM_WIDGET:
                final int itemParentId = accessor.getWidgetParentId();
                if (client.getWidgetIndex().isWidgetNotPresented(itemParentId)) {
                    break;
                }

                final WorldItemImpl worldItem = new WorldItemImpl(widget.getId());
                final Long resourceId = accessor.getCArgs().nextLong();
                worldItem.setResource(() -> client.getResourceProvider().getResourceName(resourceId));

                if (Objects.equals(itemParentId, client.getGameUiId())) {
                    final InboundMessageAccessor.RelArgsAccessor pArgs = accessor.getPArgs();
                    pArgs.nextString();
                    worldItem.setPosition(pArgs.nextPoint());
                    client.setHand(worldItem);
                    widget.setDestroy(() -> client.setHand(null));
                } else if (character.getEquip() != null && Objects.equals(itemParentId, character.getEquip().getId())) {
                    worldItem.setPosition(new IntPoint(accessor.getPArgs().nextInt(), 0));
                } else {
                    worldItem.setPosition(accessor.getPArgs().nextPoint());
                }

                addToInventoryIfPossible(widget, character::getEquip, itemParentId, worldItem);
                addToInventoryIfPossible(widget, character::getStudy, itemParentId, worldItem);
                addToInventoryIfPossible(widget, () -> client.getInventoryIndex().getInventory(itemParentId), itemParentId, worldItem);

                break;
            case ISBOX_WIDGET:
                final WorldStackImpl worldStack = new WorldStackImpl(widget.getId());
                final Number stackParentId = client.takeParentIdForNewInventory();
                if (stackParentId == null) {
                    log.warn("Parent for Stack [{}] is not provided, skipped", widget.getId());
                    break;
                }
                worldStack.setParentObjectId(stackParentId.longValue());

                final InboundMessageAccessor.RelArgsAccessor stackArgs = accessor.getCArgs();
                stackArgs.skip();
                worldStack.setCount(stackArgs.nextInt());
                worldStack.setMax(stackArgs.nextInt());
                client.getStackIndex().addStack(worldStack);
                widget.setHandleMessage(rel -> handleStackMessage(rel, worldStack));
                widget.setDestroy(() -> client.getStackIndex().removeStack(worldStack.getId()));
                break;
            case CONTEXT_MENU:
                final InboundMessageAccessor.RelArgsAccessor contextMenuArgs = accessor.getCArgs();
                final ContextMenu contextMenu = new ContextMenu(widget.getId());
                while (contextMenuArgs.hasNext()) {
                    contextMenu.addCommand(contextMenuArgs.nextString());
                }
                client.setContextMenu(contextMenu);
                widget.setDestroy(() -> client.setContextMenu(null));
                break;
        }
    }

    private void addToInventoryIfPossible(final Widget widget, final Supplier<WorldInventoryImpl> getInventory, final Integer parentId, final WorldItemImpl worldItem) {
        final WorldInventoryImpl inventory = getInventory.get();
        if (inventory == null || !Objects.equals(inventory.getId(), parentId)) {
            return;
        }

        inventory.addItem(worldItem);
        widget.setHandleMessage(rel -> handleItemMessage(rel, worldItem));
        widget.setDestroy(() -> {
            final WorldInventoryImpl inv = getInventory.get();
            if (inv != null) {
                inv.removeItem(worldItem);
            }
        });

    }

    private void handleInventoryMessage(
            final InboundMessageAccessor.RelAccessor accessor,
            final WorldInventoryImpl inventory
    ) {
        if (!Objects.equals(accessor.getWidgetMessageName(), SZ_MESSAGE_NAME)) {
            return;
        }

        final IntPoint newSize = accessor.getArgs().nextPoint();
        inventory.setSize(newSize);
    }

    @SuppressWarnings("unchecked")
    private void handleItemMessage(final InboundMessageAccessor.RelAccessor accessor, final WorldItemImpl worldItem) {
        if (!Objects.equals(accessor.getWidgetMessageName(), LABEL_MESSAGE_NAME)) {
            return;
        }


        try {
            worldItem.getArguments().addAll(client.getObjectMapper().readValue(accessor.getArgsAsString(), List.class));
        } catch (final IOException e) {
            log.error("", e);
        }
    }

    private void handleStackMessage(final InboundMessageAccessor.RelAccessor accessor, final WorldStackImpl worldStack) {
        if (!Objects.equals(accessor.getWidgetMessageName(), CHANGE_NUM_MESSAGE_NAME)) {
            return;
        }

        final InboundMessageAccessor.RelArgsAccessor stackArgs = accessor.getArgs();
        worldStack.setCount(stackArgs.nextInt());
        worldStack.setMax(stackArgs.nextInt());
    }

}
