package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.exception.UnknownWidgetException;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.evgenltd.hnhtools.common.ExecutionException;
import com.evgenltd.hnhtools.entity.IntPoint;
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

    private static final int NO_KEYBOARD_MODIFIER = 0;
    private static final int LEFT_MOUSE_BUTTON = 1;
    private static final int MIDDLE_MOUSE_BUTTON = 2;
    private static final int RIGHT_MOUSE_BUTTON = 3;

    private static final int SKIP_FLAG = -1;

    private static final int UNKNOWN_FLAG = 0;

    private static final IntPoint SCREEN_POSITION = new IntPoint();

    private ObjectMapper objectMapper;
    private ResourceState resourceState;
    private WidgetState widgetState;
    private PropState propState;
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
        this.objectMapper = objectMapper;
        this.resourceState = new ResourceState();
        this.widgetState = new WidgetState(resourceState);
        this.propState = new PropState(resourceState);
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
                    this.propState.receiveObjectData(objectData);
                    signal();
                },
                true
        );
    }

    @Override
    public List<Widget> getWidgets() {
        return widgetState.getWidgets();
    }

    @Override
    public List<Prop> getProps() {
        return propState.getProps();
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

    @Override
    public void play() {
        try {
            messageBroker.connect();
            await(messageBroker::isLife);
            messageBroker.sendRel(3, PLAY_COMMAND);
            // todo await full login
        } catch (final ExecutionException e) {
            messageBroker.disconnect();
            throw e;
        }
    }

    @Override
    public void logout() {
        messageBroker.disconnect();
    }

    @Override
    public void sendWidgetCommand(final int id, final String name, final Object... args) {
        checkWidgetId(id);
        try {
            messageBroker.sendRel(id, name, args);
        } catch (final ExecutionException e) {
            messageBroker.disconnect();
            throw e;
        }
    }

    // ##################################################
    // #                                                #
    // #  Private                                       #
    // #                                                #
    // ##################################################

    private void signal() {
        lock.lock();
        try {
            waitForReceive.signal();
        } finally {
            lock.unlock();
        }
    }

    private void checkWidgetId(final Integer widgetId) {
        final boolean hasWidget = widgetState.hasWidget(widgetId);
        if (!hasWidget) {
            throw new UnknownWidgetException(widgetId);
        }
    }

}
