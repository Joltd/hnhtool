package com.evgenltd.hnhtools.clientapp.exception;

public class UnknownWidgetException extends RuntimeException {

    public UnknownWidgetException(final Integer widgetId) {
        super(String.format("Id = [%s]", widgetId));
    }

}
