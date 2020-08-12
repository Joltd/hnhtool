package com.evgenltd.hnhtools.clientapp.widgets;

import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.List;

public interface ItemWidget extends Widget {

    String getResource();

    IntPoint getPosition();

    List<? extends ItemInfo> getItemInfoList();
}
