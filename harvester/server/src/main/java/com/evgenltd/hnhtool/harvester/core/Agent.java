package com.evgenltd.hnhtool.harvester.core;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.service.KnownObjectService;
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
    // #  Services                                      #
    // #                                                #
    // ##################################################

    KnownObjectService getKnownObjectService();

    // ##################################################
    // #                                                #
    // #  Character                                     #
    // #                                                #
    // ##################################################

    Long getCharacterId();

    IntPoint getCharacterPosition();

    // ##################################################
    // #                                                #
    // #  Commands                                      #
    // #                                                #
    // ##################################################

    void await(Supplier<Boolean> condition);

    void move(IntPoint position);

    void openContainer(KnownObject knownObject);

    void openHeap(KnownObject knownObject);

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

    Long placeHeap(IntPoint position);

    void scan();

//    void performContextMenuCommand();

}
