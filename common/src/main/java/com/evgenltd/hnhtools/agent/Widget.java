package com.evgenltd.hnhtools.agent;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 13:53</p>
 */
final class Widget {

    private Integer id;
    private String type;

    // add widget message
    Widget(final Integer id) {
        this.id = id;
    }

    // new widget message
    Widget(final Integer id, final String type) {
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
