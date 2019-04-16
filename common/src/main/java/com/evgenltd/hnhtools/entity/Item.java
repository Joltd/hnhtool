package com.evgenltd.hnhtools.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 20:22</p>
 */
public class Item {

    private Integer id;
    private Long resourceId;
    private IntPoint position;
    private List arguments = new ArrayList();

    public Item(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }



    public IntPoint getPosition() {
        return position;
    }
    public void setPosition(final IntPoint position) {
        this.position = position;
    }

    public List getArguments() {
        return arguments;
    }
    public void setArguments(final List arguments) {
        this.arguments = arguments;
    }
}
