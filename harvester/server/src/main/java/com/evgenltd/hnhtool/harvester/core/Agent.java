package com.evgenltd.hnhtool.harvester.core;

import com.evgenltd.hnhtool.harvester.core.component.agent.Character;
import com.evgenltd.hnhtool.harvester.core.component.agent.Hand;
import com.evgenltd.hnhtool.harvester.core.component.agent.Heap;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.function.Supplier;

public interface Agent {

    // ##################################################
    // #                                                #
    // #  Character                                     #
    // #                                                #
    // ##################################################

    Character.Record getCharacter();

    Heap.Record getHeap();

    Hand.Record getHand();

    Long getCurrentSpace();

    // ##################################################
    // #                                                #
    // #  Commands                                      #
    // #                                                #
    // ##################################################

    void await(Supplier<Boolean> condition);

    void move(IntPoint position);

    void moveByRoute(IntPoint position);

    void openContainer(KnownObject knownObject);

    boolean openHeap(Long knownObjectId);

    void takeItemInHandFromWorld(KnownObject knownItem);

    void takeItemInHandFromInventory(Long knownItemId);

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

    KnownObject placeHeap(IntPoint position);

    void scan();

//    void performContextMenuCommand();

}
