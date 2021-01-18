package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.exception.UnknownWidgetException;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.evgenltd.hnhtools.common.ExecutionException;
import com.evgenltd.hnhtools.messagebroker.MessageBroker;
import com.evgenltd.hnhtools.messagebroker.MessageBrokerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public final class ClientAppImpl implements ClientApp {

    private static final long DEFAULT_TIMEOUT = 60_000L;

    private static final String PLAY_COMMAND = "play";

    private final WidgetState widgetState;
    private final PropState propState;
    private final MessageBroker messageBroker;
    private final Lock lock = new ReentrantLock();
    private final Condition waitForReceive = lock.newCondition();

    public ClientAppImpl(
            @NotNull final ObjectMapper objectMapper,
            @NotNull final String host,
            final int port,
            @NotNull final String username,
            @NotNull final byte[] cookie
    ) {
        final ResourceState resourceState = new ResourceState();
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

    private void await(final Supplier<Boolean> condition) {
        await(condition, DEFAULT_TIMEOUT);
    }

    @Override
    public void await(final Supplier<Boolean> condition, final long timeout) {
        lock.lock();
        try {
            while (!condition.get()) {
                if (messageBroker.isClosed()) {
                    throw new ExecutionException("Connection to server closed");
                }
                waitForReceive.await(timeout, TimeUnit.MILLISECONDS);
            }
        } catch (final InterruptedException e) {
            throw new ExecutionException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void login() {
        try {
            messageBroker.connect();
            await(messageBroker::isLife);
        } catch (Exception e) {
            messageBroker.disconnect();
            throw e;
        }
    }

    @Override
    public void play(final String characterName) {
        try {
            messageBroker.connect();
            await(messageBroker::isLife);
            messageBroker.sendRel(3, PLAY_COMMAND, characterName);
            await(() -> widgetState.hasWidgets() && propState.hasProps());
        } catch (final ExecutionException e) {
            messageBroker.disconnect();
            throw e;
        }
    }

    @Override
    public void logout() {
        if (messageBroker.isLife()) {
            messageBroker.disconnect();
        }
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
