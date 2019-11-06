package com.evgenltd.hnhtools.messagebroker;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 06-11-2019 22:20</p>
 */
public class MessageBrokerException extends RuntimeException {

    public MessageBrokerException(final String message) {
        super(message);
    }

    public MessageBrokerException(final String template, final Object... args) {
        super(String.format(template, args));
    }

    public MessageBrokerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageBrokerException(final String template, final Throwable cause, final Object... args) {
        super(String.format(template, args), cause);
    }
}
