package com.evgenltd.hnhtools.baseclient;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-03-2019 00:32</p>
 */
final class ConnectionErrorCode {

    static final String OK = "OK";
    private static final String INVALID_AUTH_TOKEN = "INVALID_AUTH_TOKEN";
    private static final String ALREADY_LOGGED_IN = "ALREADY_LOGGED_IN";
    private static final String COULD_NOT_CONNECT = "COULD_NOT_CONNECT";
    private static final String CLIENT_TOO_OLD = "CLIENT_TOO_OLD";
    private static final String AUTH_TOKEN_EXPIRED = "AUTH_TOKEN_EXPIRED";
    private static final String UNKNOWN = "UNKNOWN";

    public static String of(final Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        switch (code) {
            case 0:
                return OK;
            case 1:
                return INVALID_AUTH_TOKEN;
            case 2:
                return ALREADY_LOGGED_IN;
            case 3:
                return COULD_NOT_CONNECT;
            case 4:
                return CLIENT_TOO_OLD;
            case 5:
                return AUTH_TOKEN_EXPIRED;
            default:
                return UNKNOWN;
        }
    }

}
