package com.evgenltd.hnhtools.messagebroker;

import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 06-11-2019 21:40</p>
 */
public interface MessageBroker {

    void setObjectMapper(@NotNull ObjectMapper objectMapper);

    /**
     * @throws MessageBrokerException if server have incorrect format
     */
    void setServer(@NotNull String host, int port);

    void setCredentials(@NotNull String username, @NotNull byte[] cookie);

    void setRelReceiver(@NotNull Consumer<InboundMessageAccessor.RelAccessor> rel);

    void setObjectDataReceiver(@NotNull Consumer<InboundMessageAccessor.ObjectDataAccessor> objectData);

    void withMonitoring();

    // ##################################################
    // #                                                #
    // #  Lifecycle                                     #
    // #                                                #
    // ##################################################

    /**
     * @throws MessageBrokerException if broker not in initial state or socket unable to be opened
     */
    void connect();

    /**
     * @throws MessageBrokerException if broker not in life or closing state
     */
    void disconnect();

    State getState();

    boolean isInit();

    boolean isConnection();

    boolean isLife();

    boolean isClosing();

    boolean isClosed();

    // ##################################################
    // #                                                #
    // #  Exchange                                      #
    // #                                                #
    // ##################################################

    void sendRel(int id, String name, Object... args);

    enum State {
        INIT,
        CONNECTION,
        LIFE,
        CLOSING,
        CLOSED
    }

}
