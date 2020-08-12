package com.evgenltd.hnhtools.messagebroker;

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
