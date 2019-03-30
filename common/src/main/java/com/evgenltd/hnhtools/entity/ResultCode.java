package com.evgenltd.hnhtools.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 16:59</p>
 */
public enum ResultCode {

    OK(true),

    INVALID_AUTH_TOKEN,
    ALREADY_LOGGED_IN,
    COULD_NOT_CONNECT,
    CLIENT_TOO_OLD,
    AUTH_TOKEN_EXPIRED,
    UNKNOWN,

    INTERRUPTED,

    NOT_REACHED,

    NO_POSITION,
    NO_MAP_VIEW,
    NO_WORLD_OBJECT,
    NO_ITEM,
    NO_CHARACTER,
    NO_CONTEXT_MENU,
    NO_CONTEXT_MENU_COMMAND;



    private boolean success;

    ResultCode() {}

    ResultCode(final boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
