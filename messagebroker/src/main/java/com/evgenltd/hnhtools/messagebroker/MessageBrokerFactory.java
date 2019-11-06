package com.evgenltd.hnhtools.messagebroker;

import com.evgenltd.hnhtools.messagebroker.impl.MessageBrokerImpl;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 06-11-2019 22:06</p>
 */
public class MessageBrokerFactory {

    public static MessageBroker buildMessaeBroker() {
        return new MessageBrokerImpl();
    }

}
