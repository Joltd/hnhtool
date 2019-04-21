package com.evgenltd.hnhtools.complexclient;

import com.evgenltd.hnhtools.complexclient.entity.impl.Widget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 23:02</p>
 */
final class WidgetIndex {

    private final Map<Integer, Widget> index = new HashMap<>();

    @NotNull
    synchronized Widget addWidget(@NotNull final Integer id, @Nullable final String type) {
        final Widget widget = new Widget(id, type);
        index.put(id, widget);
        return widget;
    }

    @Nullable
    synchronized Widget getWidget(final Integer id) {
        return index.get(id);
    }

    synchronized boolean isWidgetNotPresented(final Integer id) {
        return !index.containsKey(id);
    }

    @Nullable
    synchronized Widget removeWidget(final Integer id) {
        return index.remove(id);
    }

}
