package com.evgenltd.hnhtools.common;

public class ExecutionException extends RuntimeException {

    public ExecutionException() {}

    public ExecutionException(final String message, final Object... args) {
        super(String.format(message, args));
    }

    public ExecutionException(final Throwable cause) {
        super(cause);
    }
}
