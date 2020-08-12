package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.component.agent.Character;
import com.evgenltd.hnhtool.harvester.core.component.agent.Hand;
import com.evgenltd.hnhtool.harvester.core.component.agent.Heap;
import com.evgenltd.hnhtool.harvester.core.component.agent.Inventory;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AgentImpl implements Agent {

    private static final String CLICK_COMMAND = "click";
    private static final String TAKE_COMMAND = "take";
    private static final String DROP_COMMAND = "drop";
    private static final String ITEM_ACT_COMMAND = "itemact";
//    private static final String ITEM_ACT_SHORT_COMMAND = "iact";
//    private static final String TRANSFER_COMMAND = "transfer";
//    private static final String TRANSFER_EXT_COMMAND = "xfer";
    private static final String PLACE_COMMAND = "place";
    private static final String FOCUS_COMMAND = "focus";
    private static final String CLOSE_COMMAND = "close";
//    private static final String CONTEXT_MENU_COMMAND = "cl";

    private static final int SKIP_FLAG = -1;

    private static final int UNKNOWN_FLAG = 0;

    private static final IntPoint SCREEN_POSITION = new IntPoint();

    private MatchingService matchingService;
    private KnownObjectService knownObjectService;
    private ResourceService resourceService;

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
            final KnownObjectService knownObjectService,
            final ResourceService resourceService
    ) {
        this.matchingService = matchingService;
        this.knownObjectService = knownObjectService;
        this.resourceService = resourceService;
    }

    void setClientApp(final ClientApp clientApp) {
        this.clientApp = clientApp;

        refreshState();

        final Long characterObjectId = knownObjectService.loadCharacterObjectId(character.getCharacterName());
        character.setKnownObjectId(characterObjectId);

        final Long knownItemIdInHand = matchingService.researchHand(
                characterObjectId,
                hand.getItem()
        );
        hand.setKnownItemId(knownItemIdInHand);
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

    @Override
    public KnownObjectService getKnownObjectService() {
        return knownObjectService;
    }

    // ##################################################
    // #                                                #
    // #  State API                                     #
    // #                                                #
    // ##################################################

    @Override
    public Long getCharacterId() {
        return character.getKnownObjectId();
    }

    @Override
    public IntPoint getCharacterPosition() {
        return character.getProp().getPosition();
    }

    // ##################################################
    // #                                                #
    // #  Commands API                                  #
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
        refreshState();
        final IntPoint newPosition = character.getProp().getPosition().add(position);
        // other args if item in hand
        clientApp.sendWidgetCommand(
                mapView.getId(),
                CLICK_COMMAND,
                SCREEN_POSITION,
                newPosition,
                Mouse.LMB.code,
                KeyModifier.NO.code
        );

        await(() -> !character.getProp().isMoving() || character.getProp().getPosition().equals(newPosition));
    }

    @Override
    public void openContainer(final KnownObject knownObject) {
        refreshState();
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
    public void openHeap(final KnownObject knownObject) {
        refreshState();
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

        await(currentHeap::isOpened);

        currentHeap.setKnownObjectId(knownObject.getId());
    }

    @Override
    public void takeItemInHandFromWorld(final KnownObject knownItem) {
        hand.checkEmpty();
        refreshState();
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
        hand.checkEmpty();
        refreshState();
        final ItemWidget widget = getItemOrThrow(
                knownItem.getResource().getName(),
                knownItem.getPosition()
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
        refreshState();
        hand.checkEmpty();

        final StoreBoxWidget storeBox = currentHeap.getStoreBoxOrThrow();
        final Long heapId = currentHeap.getKnownObjectId();

        final KnownObject item = knownObjectService.loadKnownItemFromHeap(heapId);

        clientApp.sendWidgetCommand(storeBox.getId(), CLICK_COMMAND);

        await(() -> !hand.isEmpty());

        hand.setKnownItemId(item.getId());

        knownObjectService.moveToHand(character.getKnownObjectId(), item.getId(), item.getResource());

        knownObjectService.deleteHeapIfEmpty(heapId);
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
        refreshState();
        final Integer inventoryId = inventory.getWidgetOrThrow().getId();
        final ItemWidget targetItem = hand.getItemOrThrow();
        final Long knownItemId = hand.getKnownItemId();

        hand.setKnownItemId(null);

        clientApp.sendWidgetCommand(
                inventoryId,
                DROP_COMMAND,
                position
        );

        await(() -> {
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
        refreshState();
        final StoreBoxWidget storeBox = currentHeap.getStoreBoxOrThrow();
        hand.getItemOrThrow();
        final Long knownItemId = hand.getKnownItemId();

        currentHeap.checkEnoughSpace();

        final Integer currentHeapSize = storeBox.getFirst();

        clientApp.sendWidgetCommand(storeBox.getId(), DROP_COMMAND);

        await(() -> {
            if (!hand.isEmpty()) {
                return false;
            }

            if (currentHeap.getStoreBox().getFirst() <= currentHeapSize) {
                return false;
            }

            knownObjectService.moveToHeap(knownItemId, currentHeap.getKnownObjectId(), currentHeap.getStoreBox().getFirst());

            return true;
        });
    }

    @Override
    public void dropItemFromHandInWorld() {
        refreshState();
        final ItemWidget itemInHand = hand.getItemOrThrow();
        final Long knownItemId = hand.getKnownItemId();

        hand.setKnownItemId(null);

        clientApp.sendWidgetCommand(
                mapView.getId(),
                DROP_COMMAND,
                new IntPoint(),
                character.getProp().getPosition(),
                KeyModifier.NO.code
        );

        await(() -> {
            if (!hand.isEmpty()) {
                return false;
            }

            final String resource = itemInHand.getResource();
            final List<String> resources = resourceService.loadResourceOfGroup(resource);
            final Prop prop = getProp(resources, itemInHand.getPosition());
            if (prop == null) {
                return false;
            }

            knownObjectService.moveToWorld(knownItemId, prop);

            return true;
        });
    }

    @Override
    public void dropItemFromHandInEquip(final Integer position) {
        refreshState();
    }

//    private void dropItemFromInventoryInWorld(final Long knownItemId) {}

//    private void transferItem(final Long knownItemId) {}

//    private void transferItemFromCurrentHeap() {}

    @Override
    public void applyItemInHandOnObject(final Long knownObjectId) {
        refreshState();
    }

    @Override
    public void applyItemInHandOnItem(final Long knownItemId) {
        refreshState();
    }

    @Override
    public void closeCurrentInventory() {
        refreshState();
        clientApp.sendWidgetCommand(currentInventory.getWidget().getId(), CLOSE_COMMAND);
        currentInventory.setKnownObjectId(null);
        currentInventory.clearWidget();

        await(() -> !currentInventory.isOpened());
    }

    private void closeWidget(final Integer widgetId) {
        refreshState();
        clientApp.sendWidgetCommand(widgetId, CLOSE_COMMAND);

        await(() -> !widgetIndex.containsKey(widgetId));
    }

    @Override
    public Long placeHeap(final IntPoint position) {
        refreshState();
        hand.getItemOrThrow();
        final Long knownItemId = hand.getKnownItemId();
        final KnownObject knownItem = knownObjectService.findById(knownItemId);

        clientApp.sendWidgetCommand(
                mapView.getId(),
                ITEM_ACT_COMMAND,
                SCREEN_POSITION,
                position, // seems it's ignored by server
                0
        );

        clientApp.sendWidgetCommand(
                gameUi.getId(),
                FOCUS_COMMAND,
                mapView.getId()
        );

        clientApp.sendWidgetCommand(
                mapView.getId(),
                PLACE_COMMAND,
                position,
                0, // angle
                Mouse.LMB.code,
                KeyModifier.NO.code
        );

        final AtomicLong holder = new AtomicLong();

        await(() -> {
            if (!hand.isEmpty()) {
                return false;
            }

            final List<String> resources = resourceService.loadResourceOfGroup(knownItem.getResource().getName());
            final Prop heapProp = getProp(resources, position);
            if (heapProp == null) {
                return false;
            }

            final Long heapId = knownObjectService.storeHeap(
                    worldPoint.getSpace(),
                    heapProp.getPosition().add(worldPoint.getPosition()),
                    heapProp.getResource()
            );
            knownObjectService.moveToHeap(knownItemId, heapId, 1);

            holder.set(heapId);

            return true;
        });

        return holder.get();
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
        hand.setItem(null);
        currentInventory.clearWidget();
        currentHeap.clearWidget();

        for (final Widget widget : widgets) {
            switch (widget.getType()) {
                case "gameui" -> prepareGameUi(widget);
                case "mapview" -> mapView = widget;
                case "inv" -> prepareInventory((InventoryWidget) widget);
                case "item" -> prepareItem((ItemWidget) widget);
                case "isbox" -> prepareHeap((StoreBoxWidget) widget);
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

        if (currentInventory.isOpened() && !Objects.equals(currentInventory.getWidget().getId(), inventoryWidget.getId())) {
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

    @Override
    public void scan() {
        worldPoint = matchingService.researchObjects(new ArrayList<>(propIndex.values()), r -> true);
        knownObjectService.storeCharacter(character.getKnownObjectId(), worldPoint.getSpace(), character.getProp().getPosition());
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
        return getProp(Collections.singletonList(resource), position);
    }

    private Prop getProp(final List<String> resources, final IntPoint position) {
        return propIndex.values()
                .stream()
                .filter(prop -> resources.contains(prop.getResource())
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
