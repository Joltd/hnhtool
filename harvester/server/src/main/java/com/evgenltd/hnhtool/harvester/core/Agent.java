package com.evgenltd.hnhtool.harvester.core;

import com.evgenltd.hnhtools.entity.IntPoint;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 25-11-2019 18:41
 */
public interface Agent {

    // ##################################################
    // #                                                #
    // #  Commands                                      #
    // #                                                #
    // ##################################################

    void move(final IntPoint position);

    void openContainer(final Long knownObjectId);

//    void takeItemInHand(final Long knownItemId);
//
//    void takeItemInHandFromHeap();
//
//    void dropItemFromHandInInventory(final Long knownObjectId, final IntPoint position);
//
//    void dropItemFromHandInHeap();
//
//    void dropItemFromHandInWorld();
//
//    void dropItemFromHandInEquip();
//
//    void dropItemFromInventoryInWorld();
//
//    void transferItem();
//
//    void transferItemFromHeap();
//
//    void closeWidget();
//
//    void applyItemInHandOnObject();
//
//    void applyItemInHandOnItem();
//
//    void performContextMenuCommand();

}
