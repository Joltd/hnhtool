package com.evgenltd.hnhtools.complexclient.entity;

import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 21-04-2019 14:42</p>
 */
public interface WorldItem {

    Integer getId();

    String getResource();

    IntPoint getPosition();

    Integer getNumber();

    List getArguments();

}
