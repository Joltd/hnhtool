package com.evgenltd.hnhtools.clientapp.widgets;

import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 23:17</p>
 */
public interface ItemWidget extends Widget {

    String getResource();

    IntPoint getPosition();

    List<? extends ItemInfo> getItemInfoList();
}
