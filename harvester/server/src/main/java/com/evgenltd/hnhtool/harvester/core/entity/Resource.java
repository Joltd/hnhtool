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

    private boolean player = false;

    private boolean prop = false;

    private boolean container = false;

    private boolean item = false;

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

    public boolean isPlayer() {
        return player;
    }
    public void setPlayer(final boolean player) {
        this.player = player;
    }

    public boolean isProp() {
        return prop;
    }
    public void setProp(final boolean prop) {
        this.prop = prop;
    }

    public boolean isContainer() {
        return container;
    }
    public void setContainer(final boolean container) {
        this.container = container;
    }

    public boolean isItem() {
        return item;
    }
    public void setItem(final boolean item) {
        this.item = item;
    }
}
