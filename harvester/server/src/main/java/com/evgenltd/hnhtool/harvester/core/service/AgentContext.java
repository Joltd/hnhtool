package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.aspect.AgentCommand;
import com.evgenltd.hnhtool.harvester.core.component.agent.Character;
import com.evgenltd.hnhtool.harvester.core.component.agent.Hand;
import com.evgenltd.hnhtool.harvester.core.component.agent.Heap;
import com.evgenltd.hnhtool.harvester.core.component.agent.Inventory;
import com.evgenltd.hnhtool.harvester.core.component.matcher.Matcher;
import com.evgenltd.hnhtool.harvester.core.component.matcher.MatchingResult;
import com.evgenltd.hnhtool.harvester.core.component.storekeeper.Warehousing;
import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.entity.WorldPoint;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.ClientAppFactory;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.*;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.messagebroker.MessageBroker;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional
public class AgentContext {

    private static final long DEFAULT_TIMEOUT = 60_000L;

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

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private final ObjectMapper objectMapper;
    private final MatchingService matchingService;
    private final KnownObjectRepository knownObjectRepository;
    private final KnownObjectService knownObjectService;
    private final ResourceService resourceService;
    private final RoutingService routingService;
    private final Storekeeper storekeeper;

    private ClientApp clientApp;
    private Agent agent;

    private Map<Long,Prop> propIndex;
    private Map<Integer,Widget> widgetIndex;

    private Widget mapView;
    private Widget gameUi;
    private WorldPoint worldPoint; // offset = KnownObject - Prop; Prop + Offset = KnownObject
    private final Character character = new Character();
    private final Hand hand = new Hand();
    private final Inventory currentInventory = new Inventory();
    private final Heap currentHeap = new Heap();
    private final Set<Integer> forClose = new HashSet<>();

    public AgentContext(
            final ObjectMapper objectMapper,
            final MatchingService matchingService,
            final KnownObjectRepository knownObjectRepository,
            final KnownObjectService knownObjectService,
            final ResourceService resourceService,
            final RoutingService routingService,
            final Storekeeper storekeeper
    ) {
        this.objectMapper = objectMapper;
        this.matchingService = matchingService;
        this.knownObjectRepository = knownObjectRepository;
        this.knownObjectService = knownObjectService;
        this.resourceService = resourceService;
        this.routingService = routingService;
        this.storekeeper = storekeeper;
    }

    public ClientApp _getClientApp() {
        return clientApp;
    }

    public void initialize(final Agent agent) {
        this.agent = agent;
        this.clientApp = ClientAppFactory.buildClientApp(objectMapper, server, port);
    }

    public void play(final byte[] cookie) {
        clientApp.play(agent.getUsername(), cookie, agent.getCharacter());

        refreshState();

        final Long characterObjectId = knownObjectService.loadCharacterObject(character.getCharacterName()).getId();
        character.setKnownObjectId(characterObjectId);

        final Long knownItemIdInHand = matchingService.researchHand(character.getKnownObjectId(), hand.getItem());
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

    public void logout() {
        clientApp.logout();
    }

    public MessageBroker.State getConnectionState() {
        return clientApp.getState();
    }

    // ##################################################
    // #                                                #
    // #  Other API                                     #
    // #                                                #
    // ##################################################

    public Agent getAgent() {
        return agent;
    }

    public Storekeeper getStorekeeper() {
        return storekeeper;
    }

    // ##################################################
    // #                                                #
    // #  State API                                     #
    // #                                                #
    // ##################################################

    public Character.Record getCharacter() {
        return character.toRecord(worldPoint.getPosition());
    }

    private IntPoint getCharacterPosition() {
        return character.getProp().getPosition().add(worldPoint.getPosition());
    }

    public void getCharacterAttributes() {

    }

    public Heap.Record getHeap() {
        return currentHeap.toRecord();
    }

    public Hand.Record getHand() {
        return hand.toRecord();
    }

    public Long getCurrentSpace() {
        return worldPoint.getSpaceId();
    }

    // ##################################################
    // #                                                #
    // #  Commands API                                  #
    // #                                                #
    // ##################################################

    public void await(final Supplier<Boolean> condition) {
        await(condition, DEFAULT_TIMEOUT);
    }

    public void await(final Supplier<Boolean> condition, final long timeout) {
        clientApp.await(() -> {
            refreshState();
            if (character.getProp().isMoving()) {
                knownObjectService.storeCharacter(
                        character.getKnownObjectId(),
                        worldPoint.getSpaceId(),
                        getCharacterPosition()
                );
            }
            return condition.get();
        }, timeout);
    }

    @AgentCommand
    public void move(final IntPoint position) {
        refreshState();
        final IntPoint newPosition = position.sub(worldPoint.getPosition());
        // other args if item in hand
        clientApp.sendWidgetCommand(
                mapView.getId(),
                CLICK_COMMAND,
                SCREEN_POSITION,
                newPosition,
                Mouse.LMB.code,
                KeyModifier.NO.code
        );

        await(() -> !character.getProp().isMoving() || character.getProp().getPosition().equals(newPosition), DEFAULT_TIMEOUT * 5);
    }

    @AgentCommand
    public void moveByRoute(final IntPoint position) {
        final List<RoutingService.Node> route = routingService.route(
                WorldPoint.of(worldPoint.getSpaceId(), getCharacterPosition()),
                WorldPoint.of(worldPoint.getSpaceId(), position)
        );

        for (int index = 0; index < route.size() - 1; index++) {
            final RoutingService.Node node = route.get(index);
            move(node.position());
        }
    }

    @AgentCommand
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

    @AgentCommand
    public boolean openHeap(final Long knownObjectId) {
        refreshState();
        forClose.forEach(this::closeWidget);

        final KnownObject knownObject = knownObjectRepository.findOne(knownObjectId);

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

        final Integer actualCount = currentHeap.getStoreBox().getFirst();
        if (!Objects.equals(actualCount, knownObject.getChildren().size())) {
            knownObjectService.markHeapAsInvalid(knownObject);
            return false;
        }

        return true;
    }

    @AgentCommand
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

    @AgentCommand
    public void takeItemInHandFromInventory(final Long knownItemId) {
        hand.checkEmpty();
        refreshState();

        final KnownObject knownItem = knownObjectRepository.findOne(knownItemId);
        final ItemWidget widget = getItemOrThrow(knownItem.getResource().getName(), knownItem.getPosition());

        clientApp.sendWidgetCommand(
                widget.getId(),
                TAKE_COMMAND,
                SCREEN_POSITION
        );

        await(() -> !hand.isEmpty());

        hand.setKnownItemId(knownItem.getId());

        final ItemWidget itemWidget = hand.getItem();

        knownObjectService.moveToHand(character.getKnownObjectId(), knownItem.getId(), itemWidget.getResource());
    }

    @AgentCommand
    public boolean takeItemInHandFromCurrentHeap() {
        refreshState();
        hand.checkEmpty();

        if (!currentHeap.isOpened()) {
            return false;
        }

        final StoreBoxWidget storeBox = currentHeap.getStoreBox();

        clientApp.sendWidgetCommand(storeBox.getId(), CLICK_COMMAND);
        await(() -> !hand.isEmpty());

        final Long heapId = currentHeap.getKnownObjectId();
        final KnownObject heap = knownObjectRepository.findOne(heapId);
        final ItemWidget itemWidget = hand.getItem();
        final boolean isStillExists = currentHeap.isOpened();

        final List<KnownObject> knownItems = new ArrayList<>();
        final KnownObject knownItem = heap.getChildren()
                .stream()
                .max(Comparator.comparingInt(o -> o.getPosition().getX()))
                .orElse(null);
        if (knownItem != null) {
            knownItems.add(knownItem);
        }

        final MatchingResult<ItemWidget, KnownObject> result = Matcher.matchItemWidgetToKnownObject(
                Collections.singletonList(itemWidget),
                knownItems,
                Matcher.Flag.SKIP_POSITION
        );

        final boolean isItemMatched = !result.getMatches().isEmpty();

        final KnownObject character = knownObjectRepository.findOne(this.character.getKnownObjectId());
        if (knownItem != null && isItemMatched) {
            knownObjectService.moveToHand(character, knownItem, itemWidget.getResource());
            heap.getChildren().remove(knownItem);
            hand.setKnownItemId(knownItem.getId());
        } else {
            final KnownObject newKnownItem = knownObjectService.storeNewKnownItem(character, KnownObject.Place.HAND, itemWidget);
            hand.setKnownItemId(newKnownItem.getId());
        }

        if (!isItemMatched) {
            knownObjectService.markHeapAsInvalid(heap);
        }

        if (!isStillExists) {
            knownObjectService.deleteHeap(heap);
        }

        return true;
    }

    @AgentCommand
    public Long dropItemFromHandInInventory(final InventoryType type) {
        return switch (type) {
            case CURRENT -> dropItemFromHandInInventory(currentInventory, null);
            case MAIN -> dropItemFromHandInInventory(character.getMainInventory(), KnownObject.Place.MAIN_INVENTORY);
            case STUDY -> dropItemFromHandInInventory(character.getStudyInventory(), KnownObject.Place.STUDY_INVENTORY);
        };
    }

    private Long dropItemFromHandInInventory(final Inventory inventory, final KnownObject.Place place) {
        refreshState();
        final InventoryWidget inventoryWidget = inventory.getWidgetOrThrow();
        final Integer inventoryId = inventoryWidget.getId();
        final ItemWidget targetItem = hand.getItemOrThrow();
        final Long knownItemId = hand.getKnownItemId();

        final Map<String, Resource> resourceIndex = resourceService.loadResourceIndexByNames(inventory.getItems(), ItemWidget::getResource);
        final Warehousing.Box box = new Warehousing.Box(inventoryWidget.getSize());
        for (final ItemWidget item : inventory.getItems()) {
            final Resource resource = resourceIndex.get(item.getResource());
            box.fillCells(item.getPosition(), resource.getSizeOrThrow()); // possibly just return false
        }
        final Resource resource = resourceService.findByName(targetItem.getResource());
        final IntPoint suitablePosition = box.findSuitablePosition(resource.getSizeOrThrow()); // possibly just return false
        if (suitablePosition == null) {
            return null;
        }

        hand.setKnownItemId(null);

        clientApp.sendWidgetCommand(
                inventoryId,
                DROP_COMMAND,
                suitablePosition
        );

        await(() -> {
            if (!hand.isEmpty()) {
                return false;
            }

            final ItemWidget itemInInventory = getItem(inventoryId, targetItem.getResource(), suitablePosition);
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

        return knownItemId;
    }

    @AgentCommand
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

            return currentHeap.getStoreBox().getFirst() > currentHeapSize;
        });

        knownObjectService.moveToHeap(knownItemId, currentHeap.getKnownObjectId(), currentHeap.getStoreBox().getFirst());
    }

    @AgentCommand
    public void dropItemFromHandInCurrentHeapOrPlaceHeap(final IntPoint position) {
        refreshState();
        if (currentHeap.isOpened()) {
            dropItemFromHandInCurrentHeap();
        } else {
            placeHeap(position);
        }
    }

    @AgentCommand
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

    @AgentCommand
    public void dropItemFromHandInEquip(final Integer position) {
        refreshState();
    }

//    private void dropItemFromInventoryInWorld(final Long knownItemId) {}

//    private void transferItem(final Long knownItemId) {}

//    private void transferItemFromCurrentHeap() {}

    @AgentCommand
    public void applyItemInHandOnObject(final Long knownObjectId) {
        refreshState();
    }

    @AgentCommand
    public void applyItemInHandOnItem(final Long knownItemId) {
        refreshState();
    }

    @AgentCommand
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

    @AgentCommand
    public KnownObject placeHeap(final IntPoint position) {
        refreshState();

        hand.getItemOrThrow();
        final Long knownItemId = hand.getKnownItemId();
        final KnownObject knownItem = knownObjectRepository.findOne(knownItemId);
        final String resource = knownItem.getResource().getName();
        final List<String> resources = resourceService.loadResourceOfGroup(resource);

        clientApp.sendWidgetCommand(
                mapView.getId(),
                ITEM_ACT_COMMAND,
                SCREEN_POSITION,
                position, // seems it's ignored by server
                0
        );

        clientApp.sendWidgetCommand(
                mapView.getId(),
                PLACE_COMMAND,
                position,
                0, // angle
                Mouse.LMB.code,
                KeyModifier.NO.code
        );

        await(() -> {
            if (!hand.isEmpty()) {
                return false;
            }

            final Prop heapProp = getProp(resources, position);
            return heapProp != null;
        });

        final Prop heapProp = getProp(resources, position);

        final KnownObject heap = knownObjectService.storeHeap(
                worldPoint.getSpaceId(),
                heapProp.getPosition().add(worldPoint.getPosition()),
                heapProp.getResource()
        );
        knownObjectService.moveToHeap(knownItemId, heap.getId(), 1);

        return heap;
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
        character.getStudyInventory().clearWidget();
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
                case "chr" -> prepareCharacter((CharacterWidget) widget);
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

    private void prepareCharacter(final CharacterWidget widget) {
        character.setLearningPoints(widget.getLearningPoints());
        character.setExperiencePoints(widget.getExperiencePoints());
        widget.getAttributes()
                .stream()
                .map(attribute -> new Character.Attribute(attribute.name(), attribute.base(), attribute.complex()))
                .forEach(attribute -> character.getAttributes().add(attribute));
    }

    // ##################################################
    // #                                                #
    // #  Scan                                          #
    // #                                                #
    // ##################################################

    @AgentCommand
    public void scan() {
        worldPoint = matchingService.researchObjects(new ArrayList<>(propIndex.values()), r -> true);
        knownObjectService.storeCharacter(character.getKnownObjectId(), worldPoint.getSpaceId(), getCharacterPosition());
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

    public enum InventoryType {
        CURRENT,
        MAIN,
        STUDY
    }
}
