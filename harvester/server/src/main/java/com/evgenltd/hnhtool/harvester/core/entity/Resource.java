package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 03-12-2019 00:38</p>
 */
@Entity
@Table(name = "resources")
public class Resource {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private Boolean unknown = false;

    private Boolean player = false;

    private Boolean object = false;

    private Boolean doorway = false;

    private Boolean container = false;

    private Boolean heap = false;

    private Boolean item = false;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getUnknown() {
        return unknown;
    }
    public void setUnknown(final Boolean unknown) {
        this.unknown = unknown;
    }

    public Boolean getPlayer() {
        return player;
    }
    public void setPlayer(final Boolean player) {
        this.player = player;
    }

    public Boolean getObject() {
        return object;
    }
    public void setObject(final Boolean object) {
        this.object = object;
    }

    public Boolean getDoorway() {
        return doorway;
    }
    public void setDoorway(final Boolean doorway) {
        this.doorway = doorway;
    }

    public Boolean getContainer() {
        return container;
    }
    public void setContainer(final Boolean container) {
        this.container = container;
    }

    public Boolean getHeap() {
        return heap;
    }
    public void setHeap(final Boolean heap) {
        this.heap = heap;
    }

    public Boolean getItem() {
        return item;
    }
    public void setItem(final Boolean item) {
        this.item = item;
    }
}
