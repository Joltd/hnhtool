package com.evgenltd.hnhtools.messagebroker;

import com.evgenltd.hnhtools.common.ApplicationException;

public enum ObjectDeltaType {
    OD_REM(0),
    OD_MOVE(1),
    OD_RES(2),
    OD_LINBEG(3),
    OD_LINSTEP(4),
    OD_SPEECH(5),
    OD_COMPOSE(6),
    OD_ZOFF(7),
    OD_LUMIN(8),
    OD_AVATAR(9),
    OD_FOLLOW(10),
    OD_HOMING(11),
    OD_OVERLAY(12),
    @Deprecated
    OD_AUTH(13),
    OD_HEALTH(14),
    OD_BUDDY(15),
    OD_CMPPOSE(16),
    OD_CMPMOD(17),
    OD_CMPEQU(18),
    OD_ICON(19),
    OD_RESATTR(20),
    OD_END(255);

    private int value;

    ObjectDeltaType(final int value) {
        this.value = value;
    }

    public static ObjectDeltaType of(int value) {
        for (final ObjectDeltaType objectDeltaType : values()) {
            if (objectDeltaType.value == value) {
                return objectDeltaType;
            }
        }

        throw new ApplicationException("Unknown ObjectDeltaType [%s]", value);
    }

}
