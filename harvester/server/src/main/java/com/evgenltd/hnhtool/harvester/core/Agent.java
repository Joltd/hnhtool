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

    void await();

    void move(IntPoint position);

    void openContainer(Long knownObjectId);

    void openHeap(Long knownObjectId);

    void takeItemInHand(Long knownItemId);

    void takeItemInHandFromCurrentHeap();

    void dropItemFromHandInCurrentInventory(IntPoint position);

    void dropItemFromHandInMainInventory(IntPoint position);

    void dropItemFromHandInStudyInventory(IntPoint position);

    void dropItemFromHandInCurrentHeap();

    void dropItemFromHandInWorld();

    void dropItemFromHandInEquip(Integer position);

    void dropItemFromInventoryInWorld(Long knownItemId);

    void transferItem(Long knownItemId);

    void transferItemFromCurrentHeap();

    void applyItemInHandOnObject(Long knownObjectId);

    void applyItemInHandOnItem(Long knownItemId);

//    void performContextMenuCommand();

}
