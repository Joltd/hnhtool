package com.evgenltd.hnhtools.agent;

import com.evgenltd.hnhtools.baseclient.BaseClient;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.entity.ResultCode;
import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.message.RelType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

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
    private static final String CLOSE_COMMAND = "close";
    private static final String CONTEXT_MEU_COMMAND = "cl";

    private static final int LEFT_MOUSE_BUTTON = 1;
    private static final int RIGHT_MOUSE_BUTTON = 3;
    private static final int NO_KEYBOARD_MODIFIER = 0;

    private static final int SKIP_FLAG = -1;
    private static final int UNKNOWN_FLAG = 0;

    private BaseClient baseClient;
    private ResourceProvider resourceProvider;

    private String character;

    private final ObjectIndex objectIndex = new ObjectIndex();
    private final WidgetIndex widgetIndex = new WidgetIndex();

    public ComplexClient(
            @NotNull final ObjectMapper objectMapper,
            @NotNull final ResourceProvider resourceProvider,
            @NotNull final String server,
            @NotNull final Integer port,
            @NotNull final String username,
            @NotNull final byte[] cookie,
            @NotNull final String character
    ) {
        Assert.valueRequireNonEmpty(objectMapper, "ObjectMapper");
        Assert.valueRequireNonEmpty(resourceProvider, "ResourceProvider");
        Assert.valueRequireNonEmpty(server, "Server");
        Assert.valueRequireNonEmpty(port, "Port");
        Assert.valueRequireNonEmpty(username, "Username");
        Assert.valueRequireNonEmpty(cookie, "Cookie");
        Assert.valueRequireNonEmpty(character, "Character");

        this.resourceProvider = resourceProvider;
        baseClient = new BaseClient(objectMapper);
        baseClient.setServer(server, port);
        baseClient.setCredentials(username, cookie);
        baseClient.setObjectDataQueue(objectIndex::registerObjectData);
        baseClient.setRelQueue(this::registerRel);
        baseClient.withMonitor();

        this.character = character;

        objectIndex.setGetCharacterObjectId(widgetIndex::getCharacterObjectId);
    }

    private void registerRel(final InboundMessageAccessor.RelAccessor accessor) {
        final RelType type = accessor.getRelType();
        if (type == null) {
            return;
        }

        switch (type) {
            case REL_MESSAGE_NEW_WIDGET:
            case REL_MESSAGE_WIDGET_MESSAGE:
            case REL_MESSAGE_DESTROY_WIDGET:
            case REL_MESSAGE_ADD_WIDGET:
                widgetIndex.registerWidgetMessage(type, accessor);
                break;
            case REL_MESSAGE_RESOURCE_ID:
                resourceProvider.saveResource(accessor.getResourceId(), accessor.getResourceName());
                break;
            case REL_MESSAGE_CHARACTER_ATTRIBUTE:
                break;
        }
    }

    // ##################################################
    // #                                                #
    // #  API                                           #
    // #                                                #
    // ##################################################

    public boolean isLife() {
        return baseClient.isLife();
    }

    public boolean isClosed() {
        return baseClient.isClosed();
    }

    public ResultCode getConnectionErrorCode() {
        return ResultCode.valueOf(baseClient.getConnectionErrorCode().name());
    }

    public void connect() {
        baseClient.connect();
    }

    public void disconnect() {
        baseClient.disconnect();
    }

    public void play() {
        baseClient.pushOutboundRel(3, PLAY_COMMAND, character); // magic number
    }

    //

    public Result<IntPoint> getCharacterPosition() {
        return objectIndex.getCharacter()
                .map(WorldObject::getPosition);
    }

    public Result<Boolean> isCharacterMoving() {
        return objectIndex.getCharacter()
                .map(WorldObject::isMoving);
    }

    //

    public Result<Void> move(final IntPoint position) {
        final Integer mapViewId = widgetIndex.getMapViewId();
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

    public ResultCode interact(final Long objectId) {
        final Integer mapViewId = widgetIndex.getMapViewId();
        if (mapViewId == null) {
            return ResultCode.NO_MAP_VIEW;
        }

        final WorldObject worldObject = objectIndex.getWorldObject(objectId);
        if (worldObject == null) {
            return ResultCode.NO_WORLD_OBJECT;
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
                SKIP_FLAG
        );

        return ResultCode.OK;
    }

    public ResultCode takeItem(final Integer itemId) {
        final Item item = widgetIndex.getItem(itemId);
        if (item == null) {
            return ResultCode.NO_ITEM;
        }

        baseClient.pushOutboundRel(
                item.getParentId(),
                TAKE_COMMAND,
                new IntPoint() // position of cursor over item in inventory
        );

        return ResultCode.OK;
    }

    /**
     * <p>Put item from hand in inventory by particular position</p>
     */
    public ResultCode putItem(final Integer inventoryId, final IntPoint position) {
        baseClient.pushOutboundRel(
                inventoryId,
                DROP_COMMAND,
                position
        );
        return ResultCode.OK;
    }

    public ResultCode dropItem() {
        final Integer mapViewId = widgetIndex.getMapViewId();
        if (mapViewId == null) {
            return ResultCode.NO_MAP_VIEW;
        }

        final Result<WorldObject> character = objectIndex.getCharacter();
        if (character.isFailed()) {
            return character.getCode();
        }

        baseClient.pushOutboundRel(
                mapViewId,
                DROP_COMMAND,
                new IntPoint(), // cursor position on screen, server ignore it
                character.getValue().getPosition(),
                NO_KEYBOARD_MODIFIER
        );
        return ResultCode.OK;
    }

    public ResultCode closeWidget(final Integer widgetId) {
        baseClient.pushOutboundRel(widgetId, CLOSE_COMMAND);
        return ResultCode.OK;
    }

    public ResultCode contextMenuCommand(final String command) {
        final Integer contextMenuId = widgetIndex.getContextMenuId();
        if (contextMenuId == null) {
            return ResultCode.NO_CONTEXT_MENU;
        }

        final Integer contextMenuCommandId = widgetIndex.getContextMenuCommandId(command);
        if (contextMenuCommandId < 0) {
            return ResultCode.NO_CONTEXT_MENU_COMMAND;
        }

        baseClient.pushOutboundRel(
                contextMenuId,
                CONTEXT_MEU_COMMAND,
                contextMenuCommandId,
                NO_KEYBOARD_MODIFIER
        );
        return ResultCode.OK;
    }

}
