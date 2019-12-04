package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.component.agent.Character;
import com.evgenltd.hnhtool.harvester.core.component.agent.Hand;
import com.evgenltd.hnhtool.harvester.core.component.agent.Heap;
import com.evgenltd.hnhtool.harvester.core.component.agent.Inventory;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.entity.WorldPoint;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.InventoryWidget;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.clientapp.widgets.StoreBoxWidget;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.util.JsonUtil;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 25-11-2019 21:42</p>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AgentImpl implements Agent {

    private static final String CLICK_COMMAND = "click";
    private static final String TAKE_COMMAND = "take";
    private static final String DROP_COMMAND = "drop";
//    private static final String ITEM_ACT_COMMAND = "itemact";
//    private static final String ITEM_ACT_SHORT_COMMAND = "iact";
//    private static final String TRANSFER_COMMAND = "transfer";
//    private static final String TRANSFER_EXT_COMMAND = "xfer";
//    private static final String PLACE_COMMAND = "place";
    private static final String CLOSE_COMMAND = "close";
//    private static final String CONTEXT_MENU_COMMAND = "cl";

    private static final int SKIP_FLAG = -1;

    private static final int UNKNOWN_FLAG = 0;

    private static final IntPoint SCREEN_POSITION = new IntPoint();

    private MatchingService matchingService;
    private KnownObjectService knownObjectService;

    private ClientApp clientApp;

    private Map<Long,Prop> propIndex;
    private Map<Integer,Widget> widgetIndex;

    private Widget mapView;
    private Widget gameUi;
    private WorldPoint worldPoint;
    private final Character character = new Character();
    private final Hand hand = new Hand();
    private final Inventory currentInventory = new Inventory();
    private final Heap currentHeap = new Heap();
    private final Set<Integer> forClose = new HashSet<>();

    public AgentImpl(
            final MatchingService matchingService,
            final KnownObjectService knownObjectService
    ) {
        this.matchingService = matchingService;
        this.knownObjectService = knownObjectService;
    }

    void setClientApp(final ClientApp clientApp) {
        this.clientApp = clientApp;

        refreshState();

        final Long characterObjectId = knownObjectService.loadCharacterObjectId(character.getCharacterName());
        character.setKnownObjectId(characterObjectId);

        matchingService.researchItems(
                characterObjectId,
                KnownObject.Place.MAIN_INVENTORY,
                character.getMainInventory().getItems()
        );
        matchingService.researchItems(
                characterObjectId,
                KnownObject.Place.STUDY_INVENTORY,
                character.getStudyInventory().getItems()
        );
    }

    // ##################################################
    // #                                                #
    // #  API                                           #
    // #                                                #
    // ##################################################

    @Override
    public void await(final Supplier<Boolean> condition) {
        clientApp.await(() -> {
            refreshState();
            if (character.getProp().isMoving()) {
                knownObjectService.storeCharacter(
                        character.getKnownObjectId(),
                        worldPoint.getSpace(),
                        character.getProp().getPosition()
                );
            }
            return condition.get();
        });
    }

    @Override
    public void move(final IntPoint position) {
        // other args if item in hand
        clientApp.sendWidgetCommand(
                mapView.getId(),
                CLICK_COMMAND,
                SCREEN_POSITION,
                position,
                Mouse.LMB.code,
                KeyModifier.NO.code
        );

        await(() -> !character.getProp().isMoving() || character.getProp().getPosition().equals(position));
    }

    @Override
    public void openContainer(final KnownObject knownObject) {
        forClose.forEach(this::closeWidget);

        final Prop prop = getPropOrThrow(
                knownObject.getResource().getName(),
                knownObject.getPosition().sub(worldPoint.getPosition())
        );

        currentInventory.setKnownObjectId(knownObject.getId());

        clientApp.sendWidgetCommand(
                mapView.getId(),
                CLICK_COMMAND,
                SCREEN_POSITION,
                prop.getPosition(),
                Mouse.RMB.code,
                KeyModifier.NO.code,
                UNKNOWN_FLAG,
                prop.getId(),
                prop.getPosition(),
                UNKNOWN_FLAG,
                SKIP_FLAG
        );

        await(() -> {
            if (!currentInventory.isOpened()) {
                return false;
            }

            matchingService.researchItems(knownObject.getId(), null, currentInventory.getItems());
            return true;
        });
    }

    @Override
    public void openHeap(final Long knownObjectId) {

    }

    @Override
    public void takeItemInHandFromWorld(final KnownObject knownItem) {
        final Prop prop = getPropOrThrow(
                knownItem.getResource().getName(),
                knownItem.getPosition().sub(worldPoint.getPosition())
        );

        hand.setKnownItemId(knownItem.getId());

        clientApp.sendWidgetCommand(
                mapView.getId(),
                CLICK_COMMAND,
                SCREEN_POSITION,
                prop.getPosition(),
                Mouse.RMB.code,
                KeyModifier.NO.code,
                UNKNOWN_FLAG,
                prop.getId(),
                prop.getPosition(),
                UNKNOWN_FLAG,
                SKIP_FLAG
        );

        await(() -> {
            if (hand.isEmpty()) {
                return false;
            }

            final ItemWidget itemWidget = hand.getItem();

            knownObjectService.moveToHand(
                    character.getKnownObjectId(),
                    knownItem.getId(),
                    itemWidget.getResource()
            );

            return true;
        });
    }

    @Override
    public void takeItemInHandFromInventory(final KnownObject knownItem) {
        final ItemWidget widget = getItemOrThrow(
                knownItem.getResource().getName(),
                knownItem.getPosition().sub(worldPoint.getPosition())
        );

        hand.setKnownItemId(knownItem.getId());

        clientApp.sendWidgetCommand(
                widget.getId(),
                TAKE_COMMAND,
                SCREEN_POSITION
        );

        await(() -> {
            if (hand.isEmpty()) {
                return false;
            }

            final ItemWidget itemWidget = hand.getItem();

            knownObjectService.moveToHand(
                    character.getKnownObjectId(),
                    knownItem.getId(),
                    itemWidget.getResource()
            );
            return true;
        });
    }

    @Override
    public void takeItemInHandFromCurrentHeap() {

    }

    @Override
    public void dropItemFromHandInCurrentInventory(final IntPoint position) {
        dropItemFromHandInInventory(currentInventory, null, position);
    }

    @Override
    public void dropItemFromHandInMainInventory(final IntPoint position) {
        dropItemFromHandInInventory(character.getMainInventory(), KnownObject.Place.MAIN_INVENTORY, position);
    }

    @Override
    public void dropItemFromHandInStudyInventory(final IntPoint position) {
        dropItemFromHandInInventory(character.getStudyInventory(), KnownObject.Place.STUDY_INVENTORY, position);
    }

    private void dropItemFromHandInInventory(final Inventory inventory, final KnownObject.Place place, final IntPoint position) {
        final Integer inventoryId = inventory.getWidgetOrThrow().getId();
        final ItemWidget targetItem = hand.getItemorThrow();
        final Long knownItemId = hand.getKnownItemId();

        hand.setKnownItemId(null);

        clientApp.sendWidgetCommand(
                inventoryId,
                DROP_COMMAND,
                position
        );

        clientApp.await(() -> {
            if (!hand.isEmpty()) {
                return false;
            }

            final ItemWidget itemInInventory = getItem(inventoryId, targetItem.getResource(), position);
            if (itemInInventory == null) {
                return false;
            }

            knownObjectService.moveToInventory(
                    knownItemId,
                    inventory.getKnownObjectId(),
                    place,
                    itemInInventory
            );

            return true;
        });
    }

    @Override
    public void dropItemFromHandInCurrentHeap() {

    }

    @Override
    public void dropItemFromHandInWorld() {
        final ItemWidget itemInHand = hand.getItemorThrow();
        final Long knownItemId = hand.getKnownItemId();

        hand.setKnownItemId(null);

        clientApp.sendWidgetCommand(
                mapView.getId(),
                DROP_COMMAND,
                new IntPoint(),
                character.getProp().getPosition(),
                KeyModifier.NO.code
        );

        clientApp.await(() -> {
            if (!hand.isEmpty()) {
                return false;
            }

            final String resource = itemInHand.getResource(); // lookup prop resource
            final Prop prop = getProp(resource, itemInHand.getPosition());
            if (prop == null) {
                return false;
            }

            knownObjectService.moveToWorld(knownItemId, prop);

            return true;
        });
    }

    @Override
    public void dropItemFromHandInEquip(final Integer position) {

    }

//    private void dropItemFromInventoryInWorld(final Long knownItemId) {}

//    private void transferItem(final Long knownItemId) {}

//    private void transferItemFromCurrentHeap() {}

    @Override
    public void applyItemInHandOnObject(final Long knownObjectId) {

    }

    @Override
    public void applyItemInHandOnItem(final Long knownItemId) {

    }

    @Override
    public void closeCurrentInventory() {
        clientApp.sendWidgetCommand(currentInventory.getWidget().getId(), CLOSE_COMMAND);
        currentInventory.setKnownObjectId(null);
        currentInventory.clearWidget();

        await(() -> !currentInventory.isOpened());
    }

    private void closeWidget(final Integer widgetId) {
        clientApp.sendWidgetCommand(widgetId, CLOSE_COMMAND);

        await(() -> !widgetIndex.containsKey(widgetId));
    }

    // ##################################################
    // #                                                #
    // #  Refresh information about client state        #
    // #                                                #
    // ##################################################

    private void refreshState() {
        propIndex = clientApp.getProps()
                .stream()
                .collect(Collectors.toMap(Prop::getId, prop -> prop));

        final List<Widget> widgets = clientApp.getWidgets();
        widgets.sort(Comparator.comparing(Widget::getType));
        widgetIndex = widgets.stream().collect(Collectors.toMap(Widget::getId, widget -> widget));

        character.getMainInventory().clearWidget();
        character.getMainInventory().clearWidget();
        currentInventory.clearWidget();
        currentHeap.clearWidget();

        for (final Widget widget : widgets) {
            switch (widget.getType()) {
                case "gameui":
                    prepareGameUi(widget);
                    break;
                case "mapview":
                    mapView = widget;
                    break;
                case "inv":
                    prepareInventory((InventoryWidget) widget);
                    break;
                case "item":
                    prepareItem((ItemWidget) widget);
                    break;
                case "isbox":
                    prepareHeap((StoreBoxWidget) widget);
                    break;
            }
        }
    }

    private void prepareGameUi(final Widget widget) {
        gameUi = widget;

        final String characterName = JsonUtil.asText(gameUi.getArgs().get(0));
        character.setCharacterName(characterName);

        final Long characterObjectId = JsonUtil.asLong(gameUi.getArgs().get(1));
        final Prop prop = getProp(characterObjectId);
        character.setProp(prop);
    }

    private void prepareInventory(final InventoryWidget inventoryWidget) {
        final Integer parentId = inventoryWidget.getParentId();
        final Widget parent = widgetIndex.get(parentId);

        final Inventory inventory = selectInventory(parent);
        if (inventory != null) {
            inventory.setWidget(inventoryWidget);
        }

        if (!Objects.equals(currentInventory.getWidget().getId(), inventoryWidget.getId())) {
            forClose.add(inventoryWidget.getId());
        }
    }

    private Inventory selectInventory(final Widget parent) {
        if (Objects.equals(parent.getId(), gameUi.getId())) {
            return character.getMainInventory();
        } else if (Objects.equals(parent.getType(), "chr")) {
            return character.getStudyInventory();
        } else if (!currentInventory.isOpened()) {
            return currentInventory;
        } else {
            return null;
        }
    }

    private void prepareItem(final ItemWidget itemWidget) {
        final Integer parentId = itemWidget.getParentId();

        if (Objects.equals(parentId, gameUi.getId())) {
            hand.setItem(itemWidget);
            return;
        }

        final Inventory inventory = selectInventory(parentId);
        if (inventory != null) {
            inventory.getItems().add(itemWidget);
        }
    }

    private Inventory selectInventory(final Integer parentId) {
        if (Objects.equals(parentId, character.getMainInventory().getWidget().getId())) {
            return character.getMainInventory();
        } else if (Objects.equals(parentId, character.getStudyInventory().getWidget().getId())) {
            return character.getStudyInventory();
        } else if (currentInventory.isOpened() && Objects.equals(parentId, currentInventory.getWidget().getId())) {
            return currentInventory;
        } else {
            return null;
        }
    }

    private void prepareHeap(final StoreBoxWidget storeBoxWidget) {
        if (currentHeap.getStoreBox() == null) {
            currentHeap.setStoreBox(storeBoxWidget);
            return;
        }

        if (!Objects.equals(currentHeap.getStoreBox().getId(), storeBoxWidget.getId())) {
            forClose.add(storeBoxWidget.getId());
        }
    }

    // ##################################################
    // #                                                #
    // #  Scan                                          #
    // #                                                #
    // ##################################################

    public void scan() {
        worldPoint = matchingService.researchObjects(new ArrayList<>(propIndex.values()), Resource::isContainer);
    }

    // ##################################################
    // #                                                #
    // #  Private                                       #
    // #                                                #
    // ##################################################

    private Prop getProp(final Long propId) {
        final Prop prop = propIndex.get(propId);
        if (prop == null) {
            throw new ApplicationException("There is no Prop with id [%s]", propId);
        }
        return prop;
    }

    private Prop getProp(final String resource, final IntPoint position) {
        return propIndex.values()
                .stream()
                .filter(prop -> Objects.equals(prop.getResource(), resource)
                        && Objects.equals(prop.getPosition(), position))
                .findFirst()
                .orElse(null);
    }

    private Prop getPropOrThrow(final String resource, final IntPoint position) {
        final Prop prop = getProp(resource, position);
        if (prop == null) {
            throw new ApplicationException("There is no Prop with resource [%s] and position [%s]", resource, position);
        }
        return prop;
    }

    private ItemWidget getItem(@Nullable final Integer parentId, final String resource, final IntPoint position) {
        return widgetIndex.values()
                .stream()
                .filter(widget -> widget instanceof ItemWidget)
                .map(widget -> (ItemWidget) widget)
                .filter(widget -> (parentId == null || Objects.equals(widget.getParentId(), parentId))
                        && Objects.equals(widget.getResource(), resource)
                        && Objects.equals(widget.getPosition(), position))
                .findFirst()
                .orElse(null);
    }

    private ItemWidget getItemOrThrow(final String resource, final IntPoint position) {
        final ItemWidget itemWidget = getItem(null, resource, position);
        if (itemWidget == null) {
            throw new ApplicationException("There is no ItemWidget with resource [%s] and position [%s]", resource, position);
        }
        return itemWidget;
    }

    enum Mouse {
        LMB(1),
//        MMB(2),
        RMB(3);

        private int code;

        Mouse(final int code) {
            this.code = code;
        }
    }

    enum KeyModifier {
        NO(0);

        private int code;

        KeyModifier(final int code) {
            this.code = code;
        }
    }

}
