package com.evgenltd.hnhtools.messagebroker.impl.message;

import com.evgenltd.hnhtools.common.ApplicationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MessageType {
    MESSAGE_TYPE_SESSION(0),
    MESSAGE_TYPE_REL(1),
    MESSAGE_TYPE_ACKNOWLEDGE(2),
    MESSAGE_TYPE_BEAT(3),
    MESSAGE_TYPE_MAP_REQUEST(4),
    MESSAGE_TYPE_MAP_DATA(5),
    MESSAGE_TYPE_OBJECT_DATA(6),
    MESSAGE_TYPE_OBJECT_ACKNOWLEDGE(7),
    MESSAGE_TYPE_CLOSE(8);

    private final int value;

    MessageType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @NotNull
    public static MessageType of(int value) {
        for (final MessageType messageType : values()) {
            if (messageType.value == value) {
                return messageType;
            }
        }

        throw new ApplicationException("Unknown MessageType [%s]", value);
    }

    @Nullable
    public static MessageType of(String value) {
        for (final MessageType messageType : values()) {
            if (messageType.name().equals(value)) {
                return messageType;
            }
        }

        return null;
    }
}
