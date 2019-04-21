package com.evgenltd.hnhtools.complexclient.entity.impl;

import com.evgenltd.hnhtools.complexclient.entity.WorldItem;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 18-04-2019 21:01</p>
 */
public class WorldItemImpl implements WorldItem {

    private Integer id;
    private Supplier<String> resourceGetter = () -> null;

    private IntPoint position;
    private Integer number; // for equip

    private List arguments = new ArrayList();

    public WorldItemImpl(final Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getResource() {
        return resourceGetter.get();
    }
    public void setResource(final Supplier<String> resourceGetter) {
        this.resourceGetter = resourceGetter;
    }

    @Override
    public IntPoint getPosition() {
        return position;
    }
    public void setPosition(final IntPoint position) {
        this.position = position;
    }

    @Override
    public Integer getNumber() {
        return number;
    }
    public void setNumber(final Integer number) {
        this.number = number;
    }

    @Override
    public List getArguments() {
        return arguments;
    }
}
