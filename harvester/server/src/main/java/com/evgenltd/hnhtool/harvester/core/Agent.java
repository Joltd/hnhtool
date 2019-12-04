package com.evgenltd.hnhtool.harvester.core;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.function.Supplier;

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

    void await(Supplier<Boolean> condition);

    void move(IntPoint position);

    void openContainer(KnownObject knownObject);

    void openHeap(Long knownObjectId);

    void takeItemInHandFromWorld(KnownObject knownItem);

    void takeItemInHandFromInventory(KnownObject knownItem);

    void takeItemInHandFromCurrentHeap();

    void dropItemFromHandInCurrentInventory(IntPoint position);

    void dropItemFromHandInMainInventory(IntPoint position);

    void dropItemFromHandInStudyInventory(IntPoint position);

    void dropItemFromHandInCurrentHeap();

    void dropItemFromHandInWorld();

    void dropItemFromHandInEquip(Integer position);

    void applyItemInHandOnObject(Long knownObjectId);

    void applyItemInHandOnItem(Long knownItemId);

    void closeCurrentInventory();

    void scan();

//    void performContextMenuCommand();

}
