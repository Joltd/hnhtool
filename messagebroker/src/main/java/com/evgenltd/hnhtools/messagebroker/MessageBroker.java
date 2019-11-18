package com.evgenltd.hnhtools.messagebroker;


/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 06-11-2019 21:40</p>
 */
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

    void sendRel(int id, String name, Object... args) throws InterruptedException;

    enum State {
        INIT,
        CONNECTION,
        LIFE,
        CLOSING,
        CLOSED
    }

}
