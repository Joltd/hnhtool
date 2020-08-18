package com.evgenltd.hnhtool.harvester.core;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.service.KnownObjectService;
import com.evgenltd.hnhtool.harvester.core.service.MatchingService;
import com.evgenltd.hnhtool.harvester.core.service.RoutingService;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.function.Supplier;

public interface Agent {

    // ##################################################
    // #                                                #
    // #  Services                                      #
    // #                                                #
    // ##################################################

    KnownObjectService getKnownObjectService();

    MatchingService getMatchingService();

    RoutingService getRoutingService();

    // ##################################################
    // #                                                #
    // #  Character                                     #
    // #                                                #
    // ##################################################

    Long getCharacterId();

    String getCharacterName();

    IntPoint getCharacterPosition();

    Space getCurrentSpace();

    void researchHand();

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

    Long placeHeap(IntPoint position);

    void scan();

//    void performContextMenuCommand();

}
