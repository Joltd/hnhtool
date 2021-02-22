package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.component.agent.Character;
import com.evgenltd.hnhtool.harvester.core.component.agent.Hand;
import com.evgenltd.hnhtool.harvester.core.component.agent.Heap;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Task;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class A {

    private static final ThreadLocal<Context> context = new ThreadLocal<>();
    private static final Map<Long, LogEntry> forFlush = new HashMap<>();

    public static void initialize(final AgentContext agentContext, final Task task) {
        final Context context = new Context(agentContext, task);
        A.context.set(context);
    }

    public static void cleanup() {
        final Context context = A.context.get();
        if (context != null) {
            final Task task = context.getTask();
            if (task != null) {
                forFlush.remove(task.getId());
            }
        }
        A.context.remove();
    }

    // ##################################################
    // #                                                #
    // #  Logging API                                   #
    // #                                                #
    // ##################################################

    public static void info(final String message, final Object... args) {
        log(Severity.INFO, message, args);
    }

    public static void error(final Throwable throwable, final String message, final Object... args) {
        final LogEntry entry = log(Severity.ERROR, message, args);
        entry.setThrowable(throwable);
    }

    private static LogEntry log(final Severity severity, final String message, final Object... args) {
        final LogEntry logEntry = new LogEntry();
        logEntry.setSeverity(severity);
        logEntry.setMessage(String.format(message, args));

        final Context context = A.context.get();
        final LogEntry holder = context.getLogEntry();
        holder.getChildren().add(logEntry);
        sendForFlush(context);

        return logEntry;
    }

    public static void downward(final String message) {
        final Context context = A.context.get();

        final LogEntry parent = context.getLogEntry();
        final LogEntry entry = new LogEntry();
        entry.setParent(parent);
        entry.setMessage(message);
        parent.getChildren().add(entry);

        context.setLogEntry(entry);

        sendForFlush(context);
    }

    public static void upward(final String message, final long time, final Throwable throwable) {
        final Context context = A.context.get();
        final LogEntry entry = context.getLogEntry();
        entry.appendMessage(message);
        entry.setTime(time);
        entry.setThrowable(throwable);
        final LogEntry parent = entry.getParent();
        if (parent != null) {
            context.setLogEntry(parent);
        }
        sendForFlush(context);
    }

    private static synchronized void sendForFlush(final Context context) {
        forFlush.put(context.getTask().getId(), context.getRootLogEntry());
    }

    public static String logForFlush(final Function<Object,String> mapper) {
        final Context context = A.context.get();
        final LogEntry entry = context.getRootLogEntry();
        return mapper.apply(entry);
    }

    public static synchronized Map<Long, String> logsForFlush(final Function<Object,String> mapper) {
        return forFlush.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> mapper.apply(entry.getValue())
                ));
    }

    // ##################################################
    // #                                                #
    // #  Agent API                                     #
    // #                                                #
    // ##################################################

    public static AgentContext getAgentContext() {
        return context.get().getAgentContext();
    }

    public static Task getTask() {
        return context.get().getTask();
    }

    private static Storekeeper getStorekeeper() {
        return getAgentContext().getStorekeeper();
    }

    public static Character.Record getCharacter() {
        return getAgentContext().getCharacter();
    }

    public static Heap.Record getHeap() {
        return getAgentContext().getHeap();
    }

    public static Hand.Record getHand() {
        return getAgentContext().getHand();
    }

    public static Long getCurrentSpace() {
        return getAgentContext().getCurrentSpace();
    }

    public static void await(Supplier<Boolean> condition) {
        getAgentContext().await(condition);
    }

    public static void move(IntPoint position) {
        getAgentContext().move(position);
    }

    public static void moveByRoute(IntPoint position) {
        getAgentContext().moveByRoute(position);
    }

    public static void openContainer(KnownObject knownObject) {
        getAgentContext().openContainer(knownObject);
    }

    public static boolean openHeap(Long knownObjectId) {
        return getAgentContext().openHeap(knownObjectId);
    }

    public static void takeItemInHandFromWorld(KnownObject knownItem) {
        getAgentContext().takeItemInHandFromWorld(knownItem);
    }

    public static void takeItemInHandFromInventory(Long knownItemId) {
        getAgentContext().takeItemInHandFromInventory(knownItemId);
    }

    public static boolean takeItemInHandFromCurrentHeap() {
        return getAgentContext().takeItemInHandFromCurrentHeap();
    }

    public static Long dropItemFromHandInInventory(AgentContext.InventoryType type) {
        return getAgentContext().dropItemFromHandInInventory(type);
    }

    public static void dropItemFromHandInCurrentHeap() {
        getAgentContext().dropItemFromHandInCurrentHeap();
    }

    public static void dropItemFromHandInCurrentHeapOrPlaceHeap(IntPoint position) {
        getAgentContext().dropItemFromHandInCurrentHeapOrPlaceHeap(position);
    }

    public static void dropItemFromHandInWorld() {
        getAgentContext().dropItemFromHandInWorld();
    }

    public static void dropItemFromHandInEquip(Integer position) {
        getAgentContext().dropItemFromHandInEquip(position);
    }

    public static void applyItemInHandOnObject(Long knownObjectId) {
        getAgentContext().applyItemInHandOnObject(knownObjectId);
    }

    public static void applyItemInHandOnItem(Long knownItemId) {
        getAgentContext().applyItemInHandOnItem(knownItemId);
    }

    public static void closeCurrentInventory() {
        getAgentContext().closeCurrentInventory();
    }

    public static KnownObject placeHeap(IntPoint position) {
        return getAgentContext().placeHeap(position);
    }

    public static void scan() {
        getAgentContext().scan();
    }

    public static boolean store(final Long areaId, final Long itemId) {
        return getStorekeeper().store(areaId, itemId);
    }

    public static Long takeItemInInventoryFromHeap(final Long heapId, final AgentContext.InventoryType type) {
        return getStorekeeper().takeItemInInventoryFromHeap(heapId, type);
    }

    public static List<Long> takeItemsInInventoryFromHeap(final Long heapId, final AgentContext.InventoryType type) {
        return getStorekeeper().takeItemsInInventoryFromHeap(heapId, type);
    }

    private static final class Context {
        private final AgentContext agentContext;
        private final Task task;
        private final LogEntry rootLogEntry;
        private LogEntry logEntry;

        public Context(final AgentContext agentContext, final Task task) {
            this.agentContext = agentContext;
            this.task = task;
            this.logEntry = new LogEntry();
            this.rootLogEntry = this.logEntry;
        }

        public AgentContext getAgentContext() {
            return agentContext;
        }

        public Task getTask() {
            return task;
        }

        public LogEntry getRootLogEntry() {
            return rootLogEntry;
        }

        public void setLogEntry(final LogEntry logEntry) {
            this.logEntry = logEntry;
        }

        public LogEntry getLogEntry() {
            return logEntry;
        }

    }

    private static class LogEntry {
        private String message;
        private long time;
        private Severity severity = Severity.INFO;
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

        public void appendMessage(final String message) {
            this.message = this.message + message;
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
