package com.evgenltd.hnhtools.complexclient;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 23:02</p>
 */
public class WidgetIndex {

    private final Map<Integer, Widget> index = new HashMap<>();

    synchronized void addWidget(final Integer id, final String type) {
        final Widget widget = new Widget(id, type);
        index.put(id, widget);
    }

    synchronized void removeWidget(final Integer id) {
        index.remove(id);
    }

    private static final class Widget {
        private Integer id;
        private String type;

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
    }

}
