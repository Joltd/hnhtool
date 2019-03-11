package com.evgenltd.hnhtools.baseclient;

import java.util.Objects;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-03-2019 00:32</p>
 */
public enum ConnectionErrorCode {

    OK(0),
    INVALID_AUTH_TOKEN(1),
    ALREADY_LOGGED_IN(2),
    COULD_NOT_CONNECT(3),
    CLIENT_TOO_OLD(4),
    AUTH_TOKEN_EXPIRED(5),
    UNKNOWN(null);

    private Integer code;

    ConnectionErrorCode(final Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static ConnectionErrorCode of(final int code) {
        for (final ConnectionErrorCode value : values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }

        return UNKNOWN;
    }

}
