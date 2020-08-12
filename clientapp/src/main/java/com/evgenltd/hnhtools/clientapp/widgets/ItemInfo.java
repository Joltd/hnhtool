package com.evgenltd.hnhtools.clientapp.widgets;

import java.util.List;

public interface ItemInfo {

    String getResource();

    List<? extends ItemInfo> getItemInfoList();

}
