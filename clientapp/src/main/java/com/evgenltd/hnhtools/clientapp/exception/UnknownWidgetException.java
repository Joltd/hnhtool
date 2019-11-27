package com.evgenltd.hnhtools.clientapp.exception;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 27-11-2019 22:40</p>
 */
public class UnknownWidgetException extends RuntimeException {

    public UnknownWidgetException(final Integer widgetId) {
        super(String.format("Id = [%s]", widgetId));
    }

}
