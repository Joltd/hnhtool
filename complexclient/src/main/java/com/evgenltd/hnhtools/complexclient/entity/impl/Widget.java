package com.evgenltd.hnhtools.complexclient.entity.impl;

import com.evgenltd.hnhtools.message.Message;

import java.util.function.Consumer;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 19-04-2019 16:14
 */
public class Widget {
    private Integer id;
    private String type;

    private Consumer<Message.Rel> handleMessage;
    private Runnable destroy;

    public Widget(final Integer id, final String type) {
        this.id = id;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setHandleMessage(final Consumer<Message.Rel> handleMessage) {
        this.handleMessage = handleMessage;
    }

    public void setDestroy(final Runnable destroy) {
        this.destroy = destroy;
    }

    public void handleMessage(final Message.Rel accessor) {
        if (handleMessage != null) {
            handleMessage.accept(accessor);
        }
    }

    public void destroy() {
        if (destroy != null) {
            destroy.run();
        }
    }

}
