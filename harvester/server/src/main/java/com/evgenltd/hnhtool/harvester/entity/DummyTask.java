package com.evgenltd.hnhtool.harvester.entity;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 15-03-2019 13:45
 */
public class DummyTask {

    private long id;
    private String name;
    private long start;
    private long end;

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

    public long getStart() {
        return start;
    }

    public void setStart(final long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(final long end) {
        this.end = end;
    }
}
