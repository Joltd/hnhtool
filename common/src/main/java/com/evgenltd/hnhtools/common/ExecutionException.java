package com.evgenltd.hnhtools.common;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 19-11-2019 03:04</p>
 */
public class ExecutionException extends RuntimeException {

    public ExecutionException() {}

    public ExecutionException(final String message, final Object... args) {
        super(String.format(message, args));
    }

    public ExecutionException(final Throwable cause) {
        super(cause);
    }
}
