package com.evgenltd.hnhtool.harvester.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 15-03-2019 00:28</p>
 */
public final class TaskEvent {
    private Long id;
    private Long time;
    private String type;

    public TaskEvent(final Long id, final Long time, final String type) {
        this.id = id;
        this.time = time;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(final Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
