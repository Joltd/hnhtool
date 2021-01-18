package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.component.agent.Character;
import com.evgenltd.hnhtool.harvester.core.component.agent.Hand;
import com.evgenltd.hnhtool.harvester.core.component.agent.Heap;
import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.function.Supplier;

public class A {

    private static final ThreadLocal<Agent> context = new ThreadLocal<>();

    public static void set(final Agent agent) {
        context.set(agent);
    }

    public static void clear() {
        context.remove();
    }

    private static Agent getAgent() {
        return context.get();
    }

    private static Storekeeper getStorekeeper() {
        return getAgent().getStorekeeper();
    }

    public static Account getAccount() {
        return getAgent().getAccount();
    }

    public static Character.Record getCharacter() {
        return getAgent().getCharacter();
    }

    public static Heap.Record getHeap() {
        return getAgent().getHeap();
    }

    public static Hand.Record getHand() {
        return getAgent().getHand();
    }

    public static Long getCurrentSpace() {
        return getAgent().getCurrentSpace();
    }

    public static void await(Supplier<Boolean> condition) {
        getAgent().await(condition);
    }

    public static void move(IntPoint position) {
        getAgent().move(position);
    }

    public static void moveByRoute(IntPoint position) {
        getAgent().moveByRoute(position);
    }

    public static void openContainer(KnownObject knownObject) {
        getAgent().openContainer(knownObject);
    }

    public static boolean openHeap(Long knownObjectId) {
        return getAgent().openHeap(knownObjectId);
    }

    public static void takeItemInHandFromWorld(KnownObject knownItem) {
        getAgent().takeItemInHandFromWorld(knownItem);
    }

    public static void takeItemInHandFromInventory(Long knownItemId) {
        getAgent().takeItemInHandFromInventory(knownItemId);
    }

    public static boolean takeItemInHandFromCurrentHeap() {
        return getAgent().takeItemInHandFromCurrentHeap();
    }

    public static boolean dropItemFromHandInInventory(Agent.InventoryType type) {
        return getAgent().dropItemFromHandInInventory(type);
    }

    public static void dropItemFromHandInCurrentHeap() {
        getAgent().dropItemFromHandInCurrentHeap();
    }

    public static void dropItemFromHandInWorld() {
        getAgent().dropItemFromHandInWorld();
    }

    public static void dropItemFromHandInEquip(Integer position) {
        getAgent().dropItemFromHandInEquip(position);
    }

    public static void applyItemInHandOnObject(Long knownObjectId) {
        getAgent().applyItemInHandOnObject(knownObjectId);
    }

    public static void applyItemInHandOnItem(Long knownItemId) {
        getAgent().applyItemInHandOnItem(knownItemId);
    }

    public static void closeCurrentInventory() {
        getAgent().closeCurrentInventory();
    }

    public static KnownObject placeHeap(IntPoint position) {
        return getAgent().placeHeap(position);
    }

    public static void scan() {
        getAgent().scan();
    }

    public static void store(final Long areaId, final Long itemId) {
        getStorekeeper().store(areaId, itemId);
    }

    public static boolean takeItemInInventoryFromHeap(final Long heapId, final Agent.InventoryType type) {
        return getStorekeeper().takeItemInInventoryFromHeap(heapId, type);
    }

    public static void takeItemsInInventoryFromHeap(final Long heapId, final Agent.InventoryType type) {
        getStorekeeper().takeItemsInInventoryFromHeap(heapId, type);
    }

}
