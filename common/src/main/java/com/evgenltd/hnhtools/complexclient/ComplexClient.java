package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.baseclient.BaseClient;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.Character;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.entity.ResultCode;
import com.evgenltd.hnhtools.entity.WorldObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

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

    private BaseClient baseClient;
    final ResourceProvider resourceProvider;

    private final ObjectDataHandler objectDataHandler;
    private final RelMessageHandler relMessageHandler;
    final ObjectIndex objectIndex;
    final WidgetIndex widgetIndex;
    final Character character;


    public ComplexClient(
            @NotNull final ObjectMapper objectMapper,
            @NotNull final ResourceProvider resourceProvider,
            @NotNull final String server,
            @NotNull final Integer port,
            @NotNull final String username,
            @NotNull final byte[] cookie,
            @NotNull final String characterName
    ) {
        Assert.valueRequireNonEmpty(objectMapper, "ObjectMapper");
        Assert.valueRequireNonEmpty(resourceProvider, "ResourceProvider");
        Assert.valueRequireNonEmpty(server, "Server");
        Assert.valueRequireNonEmpty(port, "Port");
        Assert.valueRequireNonEmpty(username, "Username");
        Assert.valueRequireNonEmpty(cookie, "Cookie");
        Assert.valueRequireNonEmpty(characterName, "CharacterName");

        objectDataHandler = new ObjectDataHandler(this);
        relMessageHandler = new RelMessageHandler(this);
        objectIndex = new ObjectIndex();
        widgetIndex = new WidgetIndex();
        character = new Character();
        character.setName(characterName);

        this.resourceProvider = resourceProvider;
        baseClient = new BaseClient(objectMapper);
        baseClient.setServer(server, port);
        baseClient.setCredentials(username, cookie);
        baseClient.setObjectDataQueue(objectDataHandler::handleObjectData);
        baseClient.setRelQueue(relMessageHandler::handleRelMessage);
        baseClient.withMonitor();

        objectIndex.setGetCharacterObjectId(widgetIndexOld::getCharacterObjectId);
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

    public Result<WorldObject> getCharacter() {
        return objectIndex.getCharacter()
                .thenApply(this::convertWorldObject);
    }

    public Result<IntPoint> getCharacterPosition() {
        return objectIndex.getCharacter()
                .thenApply(ObjectIndex.WorldObject::getPosition);
    }

    public Result<Boolean> isCharacterMoving() {
        return objectIndex.getCharacter()
                .thenApply(ObjectIndex.WorldObject::isMoving);
    }

    // ##################################################
    // #                                                #
    // #  Object API                                    #
    // #                                                #
    // ##################################################

    public List<WorldObject> getWorldObjects() {
        return objectIndex.getObjectList()
                .stream()
                .map(this::convertWorldObject)
                .collect(Collectors.toList());
    }

    // ##################################################
    // #                                                #
    // #  Commands                                      #
    // #                                                #
    // ##################################################

    public Result<Void> move(final IntPoint position) {
        final Integer mapViewId = widgetIndexOld.getMapViewId();
        if (mapViewId == null) {
            return Result.fail(ResultCode.NO_MAP_VIEW);
        }

        baseClient.pushOutboundRel(
                mapViewId,
                CLICK_COMMAND,
                new IntPoint(), // cursor position on screen, server ignore it
                position,
                LEFT_MOUSE_BUTTON,
                NO_KEYBOARD_MODIFIER
        );

        return Result.ok();
    }

    public Result<Void> interact(final Long objectId) {
        return interact(objectId, SKIP_FLAG);
    }

    public Result<Void> interact(final Long objectId, final Integer objectElement) {
        final Integer mapViewId = widgetIndexOld.getMapViewId();
        if (mapViewId == null) {
            return Result.fail(ResultCode.NO_MAP_VIEW);
        }

        final ObjectIndex.WorldObject worldObject = objectIndex.getWorldObject(objectId);
        if (worldObject == null) {
            return Result.fail(ResultCode.NO_WORLD_OBJECT);
        }

        baseClient.pushOutboundRel(
                mapViewId,
                CLICK_COMMAND,
                new IntPoint(), // cursor position on screen, server ignore it
                worldObject.getPosition(), // here is should pe point in world, where use click RMB, but we use object position instead
                RIGHT_MOUSE_BUTTON,
                NO_KEYBOARD_MODIFIER,
                UNKNOWN_FLAG,
                objectId,
                worldObject.getPosition(),
                UNKNOWN_FLAG,
                objectElement
        );

        return Result.ok();
    }

    public Result<Void> interactItemOnObject(final Long objectId) {
        final Integer mapViewId = widgetIndexOld.getMapViewId();
        if (mapViewId == null) {
            return Result.fail(ResultCode.NO_MAP_VIEW);
        }

        final ObjectIndex.WorldObject worldObject = objectIndex.getWorldObject(objectId);
        if (worldObject == null) {
            return Result.fail(ResultCode.NO_WORLD_OBJECT);
        }

        baseClient.pushOutboundRel(
                mapViewId,
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

    public void interactItemOnItem(final Integer itemId) {
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
                UNKNOWN_FLAG
        );
    }

    public void takeItemInHand(final Integer itemId) {
        baseClient.pushOutboundRel(
                itemId,
                TAKE_COMMAND,
                new IntPoint()
        );
    }

    public void dropItemInInventory(final Integer inventoryId, final IntPoint position) {
        baseClient.pushOutboundRel(
                inventoryId,
                DROP_COMMAND,
                position
        );
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
    public Result<Void> dropItemInEquip(final Integer position) {
        final Integer characterEquipId = widgetIndexOld.getCharacterEquipId();
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
        final Integer mapViewId = widgetIndexOld.getMapViewId();
        if (mapViewId == null) {
            return Result.fail(ResultCode.NO_MAP_VIEW);
        }

        baseClient.pushOutboundRel(
                mapViewId,
                PLACE_COMMAND,
                position,
                0, // angel
                LEFT_MOUSE_BUTTON,
                NO_KEYBOARD_MODIFIER
        );

        return Result.ok();
    }

    public void closeWidget(final Integer widgetId) {
        baseClient.pushOutboundRel(widgetId, CLOSE_COMMAND);
    }

    public Result<Void> contextMenuCommand(final String command) {
        final Integer contextMenuId = widgetIndexOld.getContextMenuId();
        if (contextMenuId == null) {
            return Result.fail(ResultCode.NO_CONTEXT_MENU);
        }

        final Integer contextMenuCommandId = widgetIndexOld.getContextMenuCommandId(command);
        if (contextMenuCommandId < 0) {
            return Result.fail(ResultCode.NO_CONTEXT_MENU_COMMAND);
        }

        baseClient.pushOutboundRel(
                contextMenuId,
                CONTEXT_MEU_COMMAND,
                contextMenuCommandId,
                NO_KEYBOARD_MODIFIER
        );

        return Result.ok();
    }

    // ##################################################
    // #                                                #
    // #  Util                                          #
    // #                                                #
    // ##################################################

    // maybe move outside

    private WorldObject convertWorldObject(final ObjectIndex.WorldObject worldObject) {
        final WorldObject newWorldObject = new WorldObject(worldObject.getId());
        newWorldObject.setPosition(worldObject.getPosition());
        newWorldObject.setResourceId(worldObject.getResourceId());
        return newWorldObject;
    }

}
