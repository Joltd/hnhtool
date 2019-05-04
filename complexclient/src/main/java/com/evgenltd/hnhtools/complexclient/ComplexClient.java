package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.baseclient.BaseClient;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.entity.*;
import com.evgenltd.hnhtools.complexclient.entity.impl.*;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.entity.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 11:56</p>
 */
public final class ComplexClient {

    private static final String PLAY_COMMAND = "play";
    private static final String CLICK_COMMAND = "click";
    private static final String TAKE_COMMAND = "take";
    private static final String DROP_COMMAND = "drop";
    private static final String ITEM_ACT_COMMAND = "itemact";
    private static final String I_ACT_COMMAND = "iact";
    private static final String TRANSFER_COMMAND = "transfer";
    private static final String XFER_COMMAND = "xfer";
    private static final String PLACE_COMMAND = "place";
    private static final String CLOSE_COMMAND = "close";
    private static final String CONTEXT_MEU_COMMAND = "cl";

    private static final int LEFT_MOUSE_BUTTON = 1;
    private static final int RIGHT_MOUSE_BUTTON = 3;
    private static final int NO_KEYBOARD_MODIFIER = 0;

    private static final int SKIP_FLAG = -1;
    private static final int UNKNOWN_FLAG = 0;

    private ObjectMapper objectMapper;
    private BaseClient baseClient;
    private final ResourceProvider resourceProvider;

    private final ObjectIndex objectIndex;
    private final WidgetIndex widgetIndex;
    private final InventoryIndex inventoryIndex;
    private final StackIndex stackIndex;

    private Integer gameUiId;
    private Integer mapViewId;
    private Integer craftMenuId;
    private final CharacterImpl character;
    private WorldItemImpl hand;
    private ContextMenu contextMenu;

    private Number parentIdForNewInventory;

    public ComplexClient(
            @NotNull final ObjectMapper objectMapper,
            @NotNull final String server,
            @NotNull final Integer port,
            @NotNull final String username,
            @NotNull final byte[] cookie,
            @NotNull final String characterName
    ) {
        Assert.valueRequireNonEmpty(objectMapper, "ObjectMapper");
        Assert.valueRequireNonEmpty(server, "Server");
        Assert.valueRequireNonEmpty(port, "Port");
        Assert.valueRequireNonEmpty(username, "Username");
        Assert.valueRequireNonEmpty(cookie, "Cookie");
        Assert.valueRequireNonEmpty(characterName, "CharacterName");

        this.objectMapper = objectMapper;

        final ObjectDataHandler objectDataHandler = new ObjectDataHandler(this);
        final RelMessageHandler relMessageHandler = new RelMessageHandler(this);
        objectIndex = new ObjectIndex();
        widgetIndex = new WidgetIndex();
        inventoryIndex = new InventoryIndex();
        stackIndex = new StackIndex();
        character = new CharacterImpl();
        character.setName(characterName);

        this.resourceProvider = new ResourceProvider();
        baseClient = new BaseClient(objectMapper);
        baseClient.setServer(server, port);
        baseClient.setCredentials(username, cookie);
        baseClient.setObjectDataQueue(objectDataHandler::handleObjectData);
        baseClient.setRelQueue(relMessageHandler::handleRelMessage);
        baseClient.withMonitor();

    }

    // ##################################################
    // #                                                #
    // #  Private Accessors                             #
    // #                                                #
    // ##################################################

    ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    ObjectIndex getObjectIndex() {
        return objectIndex;
    }

    WidgetIndex getWidgetIndex() {
        return widgetIndex;
    }

    InventoryIndex getInventoryIndex() {
        return inventoryIndex;
    }

    StackIndex getStackIndex() {
        return stackIndex;
    }

    Integer getGameUiId() {
        return gameUiId;
    }
    synchronized void setGameUiId(final Integer gameUiId) {
        this.gameUiId = gameUiId;
    }

    private Result<Integer> getMapViewId() {
        return mapViewId != null
                ? Result.ok(mapViewId)
                : Result.fail(ResultCode.NO_MAP_VIEW);
    }
    synchronized void setMapViewId(final Integer mapViewId) {
        this.mapViewId = mapViewId;
    }

    public WorldItem getHand() {
        return hand;
    }
    void setHand(final WorldItemImpl hand) {
        this.hand = hand;
    }

    Integer getCraftMenuId() {
        return craftMenuId;
    }
    synchronized void setCraftMenuId(final Integer craftMenuId) {
        this.craftMenuId = craftMenuId;
    }

    void setContextMenu(final ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }
    public List<String> getContextMenuCommands() {
        return contextMenu != null
                ? Collections.unmodifiableList(contextMenu.getCommands())
                : Collections.emptyList();
    }
    public boolean hasContextMenu() {
        return contextMenu != null;
    }

    // ##################################################
    // #                                                #
    // #  Connection API                                #
    // #                                                #
    // ##################################################

    public boolean isLife() {
        return baseClient.isLife();
    }

    public boolean isClosed() {
        return baseClient.isClosed();
    }

    public String getConnectionErrorCode() {
        return baseClient.getConnectionErrorCode();
    }

    public void connect() {
        baseClient.connect();
    }

    public void disconnect() {
        baseClient.disconnect();
    }

    public void play() {
        baseClient.pushOutboundRel(3, PLAY_COMMAND, character.getName()); // magic number
    }

    // ##################################################
    // #                                                #
    // #  Character API                                 #
    // #                                                #
    // ##################################################

    CharacterImpl getCharacter() {
        return character;
    }

    public Long getCharacterId() {
        return character.getId();
    }

    public Result<WorldObject> getCharacterObject() {
        final WorldObjectImpl object = objectIndex.getObject(getCharacter().getId());
        return object != null
                ? Result.ok(object)
                : Result.fail(ResultCode.NO_CHARACTER);
    }

    public Result<IntPoint> getCharacterPosition() {
        return getCharacterObject()
                .thenApply(WorldObject::getPosition);
    }

    public Result<Boolean> isCharacterMoving() {
        final WorldObjectImpl object = objectIndex.getObject(getCharacter().getId());
        return object != null
                ? Result.ok(object.isMoving())
                : Result.fail(ResultCode.NO_CHARACTER);
    }

    // ##################################################
    // #                                                #
    // #  Object API                                    #
    // #                                                #
    // ##################################################

    public List<WorldObject> getWorldObjects() {
        return objectIndex.getObjectList();
    }

    // ##################################################
    // #                                                #
    // #  Inventory API                                 #
    // #                                                #
    // ##################################################

    Number takeParentIdForNewInventory() {
        final Number parentId = this.parentIdForNewInventory;
        parentIdForNewInventory = null;
        return parentId;
    }

    public boolean parentIdIsTaken() {
        return parentIdForNewInventory == null;
    }

    /**
     * @param parentIdForNewInventory should be id in context of world objects
     */
    public Result<Void> setParentIdForNewInventory(final Number parentIdForNewInventory) {
        if (this.parentIdForNewInventory != null) {
            return Result.fail(ResultCode.ANOTHER_INVENTORY_ALREADY_QUEUED_WOR_OPENNING);
        }
        this.parentIdForNewInventory = parentIdForNewInventory;
        return Result.ok();
    }

    public List<WorldInventory> getInventories() {
        return inventoryIndex.getInventories();
    }

    public List<WorldStack> getStacks() {
        return stackIndex.getStacks();
    }

    public Result<WorldStack> getStack(final Long parentId) {
        final WorldStack stack = stackIndex.getStackByParentId(parentId);
        return stack != null ? Result.ok(stack) : Result.fail(ResultCode.NO_STACK);
    }

    // ##################################################
    // #                                                #
    // #  Commands                                      #
    // #                                                #
    // ##################################################

    public Result<Void> move(final IntPoint position) {
        return getMapViewId().thenApply(mapViewId -> {
            baseClient.pushOutboundRel(
                    mapViewId,
                    CLICK_COMMAND,
                    new IntPoint(), // cursor position on screen, server ignore it
                    position,
                    LEFT_MOUSE_BUTTON,
                    NO_KEYBOARD_MODIFIER
            );
            return null;
        });
    }

    public Result<Void> interact(final Long objectId) {
        return interact(objectId, SKIP_FLAG);
    }

    public Result<Void> interact(final Long objectId, final Integer objectElement) {
        final Result<Integer> mapViewId = getMapViewId();
        if (mapViewId.isFailed()) {
            return mapViewId.cast();
        }

        final Result<WorldObjectImpl> worldObject = objectIndex.getWorldObjectIfPossible(objectId);
        if (worldObject.isFailed()) {
            return worldObject.cast();
        }

        baseClient.pushOutboundRel(
                mapViewId.getValue(),
                CLICK_COMMAND,
                new IntPoint(), // cursor position on screen, server ignore it
                worldObject.getValue().getPosition(), // here is should pe point in world, where use click RMB, but we use object position instead
                RIGHT_MOUSE_BUTTON,
                NO_KEYBOARD_MODIFIER,
                UNKNOWN_FLAG,
                objectId,
                worldObject.getValue().getPosition(),
                UNKNOWN_FLAG,
                objectElement
        );
        return Result.ok();
    }

    public Result<Void> interactItemInHandOnObject(final Long objectId) {
        final Result<Integer> mapViewId = getMapViewId();
        if (mapViewId.isFailed()) {
            return mapViewId.cast();
        }

        final WorldObjectImpl worldObject = objectIndex.getWorldObject(objectId);
        if (worldObject == null) {
            return Result.fail(ResultCode.NO_WORLD_OBJECT);
        }

        baseClient.pushOutboundRel(
                mapViewId.getValue(),
                ITEM_ACT_COMMAND,
                new IntPoint(), // cursor position on screen, server ignore it
                worldObject.getPosition(), // here is should pe point in world, where use click RMB, but we use object position instead
                UNKNOWN_FLAG,
                UNKNOWN_FLAG,
                objectId,
                worldObject.getPosition(),
                UNKNOWN_FLAG,
                UNKNOWN_FLAG
        );

        return Result.ok();
    }

    public void interactItemInHandOnItem(final Integer itemId) {
        baseClient.pushOutboundRel(
                itemId,
                ITEM_ACT_COMMAND,
                UNKNOWN_FLAG
        );
    }

    public void interactWithItem(final Integer itemId) {
        baseClient.pushOutboundRel(
                itemId,
                I_ACT_COMMAND,
                new IntPoint(),
                NO_KEYBOARD_MODIFIER
        );
    }

    public void takeItemInHand(final Integer itemId) {
        baseClient.pushOutboundRel(
                itemId,
                TAKE_COMMAND,
                new IntPoint()
        );
    }

    public void takeItemInHandFromStack(final Integer stackId) {
        baseClient.pushOutboundRel(stackId, CLICK_COMMAND);
    }

    public void dropItemFromHandInInventory(final Integer inventoryId, final IntPoint position) {
        baseClient.pushOutboundRel(
                inventoryId,
                DROP_COMMAND,
                position
        );
    }

    public void dropItemFromHandInStack(final Integer stackId) {
        baseClient.pushOutboundRel(
                stackId,
                DROP_COMMAND
        );
    }

    public Result<Void> dropItemFromHandInWorld() {
        final Result<Integer> mapViewId = getMapViewId();
        if (mapViewId.isFailed()) {
            return mapViewId.cast();
        }

        final WorldObjectImpl character = objectIndex.getObject(this.character.getId());
        baseClient.pushOutboundRel(
                mapViewId.getValue(),
                DROP_COMMAND,
                new IntPoint(),
                character.getPosition(),
                NO_KEYBOARD_MODIFIER
        );
        return Result.ok();
    }

    public void dropItemInWorld(final Integer itemId) {
        baseClient.pushOutboundRel(
                itemId,
                DROP_COMMAND,
                new IntPoint()
        );
    }

    /**
     * @param position position in equip, -1 and server determine it itself
     */
    public Result<Void> dropItemFromHandInEquip(final Integer position) {
        final Integer characterEquipId = getCharacter().getEquip().getId();
        if (characterEquipId == null) {
            return Result.fail(ResultCode.NO_INVENTORY);
        }

        baseClient.pushOutboundRel(
                characterEquipId,
                DROP_COMMAND,
                position
        );
        return Result.ok();
    }

    public void transferItem(final Integer itemId) {
        baseClient.pushOutboundRel(
                itemId,
                TRANSFER_COMMAND,
                new IntPoint()
        );
    }

    /**
     * @param stackId id of isbox widget
     */
    public void transferItemFromStack(final Integer stackId) {
        baseClient.pushOutboundRel(
                stackId,
                XFER_COMMAND
        );
    }

    public Result<Void> placeStack(final IntPoint position) {
        return getMapViewId().thenApply(mapViewId -> {
            baseClient.pushOutboundRel(
                    mapViewId,
                    PLACE_COMMAND,
                    position,
                    0, // angel
                    LEFT_MOUSE_BUTTON,
                    NO_KEYBOARD_MODIFIER
            );
            return null;
        });
    }

    public Result<Void> closeInventory(final Integer inventoryId) {
        final WorldInventoryImpl inventory = inventoryIndex.getInventory(inventoryId);
        if (inventory == null) {
            return Result.fail(ResultCode.NO_INVENTORY);
        }

        baseClient.pushOutboundRel(inventory.getWindowId(), CLOSE_COMMAND);
        return Result.ok();
    }

    public Result<Void> closeStack(final Integer stackId) {
        final WorldStackImpl stack = stackIndex.getStack(stackId);
        if (stack == null) {
            return Result.fail(ResultCode.NO_STACK);
        }

        baseClient.pushOutboundRel(stack.getWindowId(), CLOSE_COMMAND);
        return Result.ok();
    }

    public Result<Void> contextMenuCommand(final String command) {
        if (contextMenu == null) {
            return Result.fail(ResultCode.NO_CONTEXT_MENU);
        }

        final int commandIndex = contextMenu.getCommands().indexOf(command);
        if (commandIndex < 0) {
            return Result.fail(ResultCode.NO_CONTEXT_MENU_COMMAND);
        }

        baseClient.pushOutboundRel(
                contextMenu.getId(),
                CONTEXT_MEU_COMMAND,
                commandIndex,
                NO_KEYBOARD_MODIFIER
        );

        return Result.ok();
    }

}
