package com.evgenltd.hnhtools.message;

import com.evgenltd.hnhtools.common.ApplicationException;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 15:09</p>
 */
public enum RelType {
    REL_MESSAGE_NEW_WIDGET(0),
    REL_MESSAGE_WIDGET_MESSAGE(1),
    REL_MESSAGE_DESTROY_WIDGET(2),
    REL_MESSAGE_MAPIV(3),
    REL_MESSAGE_GLOBLOB(4),
    @Deprecated
    REL_MESSAGE_PAGINAE(5),
    REL_MESSAGE_RESOURCE_ID(6),
    REL_MESSAGE_PARTY(7),
    REL_MESSAGE_SFX(8),
    REL_MESSAGE_CHARACTER_ATTRIBUTE(9),
    REL_MESSAGE_MUSIC(10),
    REL_MESSAGE_TILES(11),
    @Deprecated
    REL_MESSAGE_BUFF(12),
    REL_MESSAGE_SESSION_KEY(13),
    REL_MESSAGE_FRAGMENT(14),
    REL_MESSAGE_ADD_WIDGET(15);

    private int value;

    RelType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RelType of(int value) {
        for (final RelType relType : values()) {
            if (relType.value == value) {
                return relType;
            }
        }

        throw new ApplicationException("Unknown RelType [%s]", value);
    }
}
