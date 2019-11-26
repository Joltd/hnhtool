package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.WorldObject;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.evgenltd.hnhtools.common.ExecutionException;
import com.evgenltd.hnhtools.messagebroker.MessageBroker;
import com.evgenltd.hnhtools.messagebroker.MessageBrokerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-11-2019 00:58</p>
 */
public final class ClientAppImpl implements ClientApp {

    private static final long TIMEOUT = 1000;

    private static final String PLAY_COMMAND = "play";
    private static final String CLICK_COMMAND = "click";
    private static final String TAKE_COMMAND = "take";
    private static final String DROP_COMMAND = "drop";
    private static final String ITEM_ACT_COMMAND = "itemact";
    private static final String ITEM_ACT_SHORT_COMMAND = "iact";
    private static final String TRANSFER_COMMAND = "transfer";
    private static final String TRANSFER_EXT_COMMAND = "xfer";
    private static final String PLACE_COMMAND = "place";
    private static final String CLOSE_COMMAND = "close";
    private static final String CONTEXT_MENU_COMMAND = "cl";

    private ResourceState resourceState;
    private WidgetState widgetState;
    private WorldObjectState worldObjectState;
    private MessageBroker messageBroker;
    private Lock lock = new ReentrantLock();
    private Condition waitForReceive = lock.newCondition();

    public ClientAppImpl(
            @NotNull final ObjectMapper objectMapper,
            @NotNull final String host,
            final int port,
            @NotNull final String username,
            @NotNull final byte[] cookie
    ) {
        this.resourceState = new ResourceState();
        this.widgetState = new WidgetState(resourceState);
        this.worldObjectState = new WorldObjectState(resourceState);
        this.messageBroker = MessageBrokerFactory.buildMessageBroker(
                objectMapper,
                host,
                port,
                username,
                cookie,
                rel -> {
                    this.widgetState.receiveRel(rel);
                    signal();
                },
                objectData -> {
                    this.worldObjectState.receiveObjectData(objectData);
                    signal();
                },
                true
        );
    }

    @Override
    public List<Widget> getWidgets() {
        return null;
    }

    @Override
    public List<WorldObject> getWorldObjects() {
        return null;
    }

    @Override
    public void await(final Supplier<Boolean> condition) {
        lock.lock();
        try {
            while (!condition.get()) {
                waitForReceive.await();
            }
        } catch (final InterruptedException e) {
            throw new ExecutionException(e);
        } finally {
            lock.unlock();
        }
    }

    private void signal() {
        lock.lock();
        try {
            waitForReceive.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void play() {
        try {
            messageBroker.connect();
            await(messageBroker::isLife);
            messageBroker.sendRel(3, PLAY_COMMAND);
        } catch (final ExecutionException e) {
            messageBroker.disconnect();
            throw e;
        }
    }

    @Override
    public void logout() {
        messageBroker.disconnect();
    }

    private void sendRel(final int id, final String name, final Object... args) {
        try {
            messageBroker.sendRel(id, name, args);
        } catch (final ExecutionException e) {
            messageBroker.disconnect();
            throw e;
        }
    }

    @Override
    public void click() {
//        baseClient.pushOutboundRel(
//                mapViewId,
//                CLICK_COMMAND,
//                new IntPoint(), // cursor position on screen, server ignore it
//                position,
//                LEFT_MOUSE_BUTTON,
//                NO_KEYBOARD_MODIFIER
//        );
//        baseClient.pushOutboundRel(
//                mapViewId.getValue(),
//                CLICK_COMMAND,
//                new IntPoint(), // cursor position on screen, server ignore it
//                worldObject.getValue().getPosition(), // here is should pe point in world, where use click RMB, but we use object position instead
//                RIGHT_MOUSE_BUTTON,
//                NO_KEYBOARD_MODIFIER,
//                UNKNOWN_FLAG,
//                objectId,
//                worldObject.getValue().getPosition(),
//                UNKNOWN_FLAG,
//                objectElement
//        );
//        baseClient.pushOutboundRel(stackId, CLICK_COMMAND);

        // widget id (map view id)
        // CLICK
        // position on screen
        // position in world
        // mouse button
        // keyboard modifier
        // -
        // object id in world
        // position
        // -
        // object element
    }

    @Override
    public void take() {

    }

    @Override
    public void drop() {

    }

    @Override
    public void itemAct() {

    }

    @Override
    public void itemActShort() {

    }

    @Override
    public void transfer() {

    }

    @Override
    public void transferExt() {

    }

    @Override
    public void place() {

    }

    @Override
    public void close() {

    }

    @Override
    public void contextMenu() {

    }

    // ##################################################
    // #                                                #
    // #  Private                                       #
    // #                                                #
    // ##################################################

}
