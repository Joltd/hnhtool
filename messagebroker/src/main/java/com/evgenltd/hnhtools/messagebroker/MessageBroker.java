package com.evgenltd.hnhtools.messagebroker;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

public interface MessageBroker {

    // ##################################################
    // #                                                #
    // #  Lifecycle                                     #
    // #                                                #
    // ##################################################

    /**
     * @throws MessageBrokerException if broker not in initial state or socket unable to be opened
     * or if server have incorrect format
     */
    void connect(String username, byte[] cookie);

    /**
     * @throws MessageBrokerException if broker not in life or closing state
     */
    void disconnect();

    State getState();

    Status getStatus();

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

    enum Status {
        INIT,
        CONNECTION,
        LIFE,
        CLOSING,
        CLOSED
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    record State(
            String username,
            Status status,
            Thread.State inbound,
            Thread.State outbound,
            SocketState socketState
    ) {}

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    record SocketState(boolean connected, boolean closed, boolean bound) {}

}
