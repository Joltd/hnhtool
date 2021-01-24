package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.component.agent.Character;
import com.evgenltd.hnhtool.harvester.core.component.agent.Hand;
import com.evgenltd.hnhtool.harvester.core.component.agent.Heap;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class A {

    private static final ThreadLocal<Context> context = new ThreadLocal<>();

    public static void init(final AgentContext agent) {
        if (context.get() != null) {
            throw new IllegalStateException("Context already initialized");
        }

        final Context contextData = new Context();
        contextData.setAgent(agent);
        contextData.setLogEntry(new LogEntry());
        context.set(contextData);
    }

    public static void remove() {
        context.remove();
    }

    // ##################################################
    // #                                                #
    // #  Logging API                                   #
    // #                                                #
    // ##################################################


    public static void log() {

    }

    public static void info() {

    }

    public static void error() {

    }

    public static void downward() {
        final LogEntry entry = new LogEntry();

        final LogEntry parent = context.get().getLogEntry();
        if (parent != null) {
            entry.setParent(parent);
            parent.getChildren().add(entry);
        }

        context.get().setLogEntry(entry);
    }

    public static void upward() {
        final LogEntry entry = context.get().getLogEntry();
        if (entry == null) {
            return;
        }

        if (entry.getParent() != null) {
            context.get().setLogEntry(entry.getParent());
        }
    }

    // ##################################################
    // #                                                #
    // #  Agent API                                     #
    // #                                                #
    // ##################################################

    private static AgentContext getAgent() {
        return context.get().getAgent();
    }

    private static Storekeeper getStorekeeper() {
        return getAgent().getStorekeeper();
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

    public static boolean dropItemFromHandInInventory(AgentContext.InventoryType type) {
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

    public static boolean takeItemInInventoryFromHeap(final Long heapId, final AgentContext.InventoryType type) {
        return getStorekeeper().takeItemInInventoryFromHeap(heapId, type);
    }

    public static void takeItemsInInventoryFromHeap(final Long heapId, final AgentContext.InventoryType type) {
        getStorekeeper().takeItemsInInventoryFromHeap(heapId, type);
    }

    private static final class Context {
        private AgentContext agent;
        private LogEntry logEntry;

        public AgentContext getAgent() {
            return agent;
        }

        public void setAgent(final AgentContext agent) {
            this.agent = agent;
        }

        public LogEntry getLogEntry() {
            return logEntry;
        }

        public void setLogEntry(final LogEntry logEntry) {
            this.logEntry = logEntry;
        }
    }

    public static class LogEntry {
        private String message;
        private long time;
        private Severity severity;
        private Throwable throwable;
        private List<LogEntry> children = new ArrayList<>();

        @JsonIgnore
        private LogEntry parent;

        public String getMessage() {
            return message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public long getTime() {
            return time;
        }

        public void setTime(final long time) {
            this.time = time;
        }

        public Severity getSeverity() {
            return severity;
        }

        public void setSeverity(final Severity severity) {
            this.severity = severity;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(final Throwable throwable) {
            this.throwable = throwable;
        }

        public List<LogEntry> getChildren() {
            return children;
        }

        public void setChildren(final List<LogEntry> children) {
            this.children = children;
        }

        public LogEntry getParent() {
            return parent;
        }

        public void setParent(final LogEntry parent) {
            this.parent = parent;
        }
    }

    public enum Severity {
        INFO,
        ERROR
    }


}
