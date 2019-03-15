package com.evgenltd.hnhtool.harvester.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 15-03-2019 00:28</p>
 */
public final class TaskEvent {

    private long id;
    private String name;
    private long time;
    private Type type;

    private TaskEvent() {}

    public static TaskEvent start(final long id, final String name, final long time) {
        final TaskEvent event = new TaskEvent();
        event.id = id;
        event.name = name;
        event.time = time;
        event.type = Type.START;
        return event;
    }

    public static TaskEvent end(final long id, final String name, final long time) {
        final TaskEvent event = new TaskEvent();
        event.id = id;
        event.name = name;
        event.time = time;
        event.type = Type.END;
        return event;
    }

    public long getId() {
        return id;
    }
    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }
    public void setTime(final long time) {
        this.time = time;
    }

    public Type getType() {
        return type;
    }
    public void setType(final Type type) {
        this.type = type;
    }

    enum Type {
        START,
        END
    }

}
