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
    REL_MESSAGE_NEW_WIDGET,
    REL_MESSAGE_WIDGET_MESSAGE,
    REL_MESSAGE_DESTROY_WIDGET,
    REL_MESSAGE_MAPIV,
    REL_MESSAGE_GLOBLOB,
    @Deprecated
    REL_MESSAGE_PAGINAE,
    REL_MESSAGE_RESOURCE_ID,
    REL_MESSAGE_PARTY,
    REL_MESSAGE_SFX,
    REL_MESSAGE_CHARACTER_ATTRIBUTE,
    REL_MESSAGE_MUSIC,
    REL_MESSAGE_TILES,
    @Deprecated
    REL_MESSAGE_BUFF,
    REL_MESSAGE_SESSION_KEY,
    REL_MESSAGE_FRAGMENT,
    REL_MESSAGE_ADD_WIDGET;

    public static RelType of(int value) {
        for (final RelType relType : values()) {
            if (relType.ordinal() == value) {
                return relType;
            }
        }

        throw new ApplicationException("Unknown RelType [%s]", value);
    }
}
