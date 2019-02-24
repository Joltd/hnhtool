package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.common.ApplicationException;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 02:04</p>
 */
public enum MessageType {
    MESSAGE_TYPE_SESSION,
    MESSAGE_TYPE_REL,
    MESSAGE_TYPE_ACKNOWLEDGE,
    MESSAGE_TYPE_BEAT,
    MESSAGE_TYPE_MAP_REQUEST,
    MESSAGE_TYPE_MAP_DATA,
    MESSAGE_TYPE_OBJECT_DATA,
    MESSAGE_TYPE_OBJECT_ACKNOWLEDGE,
    MESSAGE_TYPE_CLOSE;

    public static MessageType of(int value) {
        for (final MessageType messageType : values()) {
            if (messageType.ordinal() == value) {
                return messageType;
            }
        }

        throw new ApplicationException("Unknown MessageType [%s]", value);
    }
}
