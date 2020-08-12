package com.evgenltd.hnhtools.messagebroker;

public class MessageBrokerException extends RuntimeException {

    public MessageBrokerException(final String template, final Object... args) {
        super(String.format(template, args));
    }

    public MessageBrokerException(final String template, final Throwable cause, final Object... args) {
        super(String.format(template, args), cause);
    }

}
