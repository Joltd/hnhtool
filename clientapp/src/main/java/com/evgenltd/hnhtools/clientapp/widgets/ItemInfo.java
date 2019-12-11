package com.evgenltd.hnhtools.clientapp.widgets;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-12-2019 21:00</p>
 */
public interface ItemInfo {

    String getResource();

    List<? extends ItemInfo> getItemInfoList();

}
