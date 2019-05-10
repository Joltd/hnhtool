package com.evgenltd.hnhtool.harvester.common.entity;

import com.evgenltd.hnhtools.entity.IntPoint;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 22:59</p>
 */
@Entity
@Table(name = "known_objects")
public class KnownObject implements Identified {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Space owner;

    private String resource;

    private Integer x;

    private Integer y;

    private LocalDateTime actual;

    private Boolean researched = false;

    private Boolean player = false;

    private Boolean doorway = false;

    private Boolean item = false;

    private Boolean container = false;

    private Boolean stack = false;

    private Integer count;

    private Integer max;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public Space getOwner() {
        return owner;
    }
    public void setOwner(final Space owner) {
        this.owner = owner;
    }

    public String getResource() {
        return resource;
    }
    public void setResource(final String resource) {
        this.resource = resource;
    }

    public Integer getX() {
        return x;
    }
    public void setX(final Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }
    public void setY(final Integer y) {
        this.y = y;
    }

    public IntPoint getPosition() {
        return new IntPoint(x, y);
    }

    public LocalDateTime getActual() {
        return actual;
    }
    public void setActual(final LocalDateTime actual) {
        this.actual = actual;
    }

    public Boolean getResearched() {
        return researched;
    }
    public void setResearched(final Boolean researched) {
        this.researched = researched;
    }

    public Boolean getPlayer() {
        return player;
    }
    public void setPlayer(final Boolean player) {
        this.player = player;
    }

    public Boolean getDoorway() {
        return doorway;
    }
    public void setDoorway(final Boolean doorway) {
        this.doorway = doorway;
    }

    public Boolean getItem() {
        return item;
    }
    public void setItem(final Boolean item) {
        this.item = item;
    }

    public Boolean getContainer() {
        return container;
    }
    public void setContainer(final Boolean container) {
        this.container = container;
    }

    public Boolean getStack() {
        return stack;
    }
    public void setStack(final Boolean stack) {
        this.stack = stack;
    }

    public Integer getCount() {
        return count;
    }
    public void setCount(final Integer count) {
        this.count = count;
    }

    public Integer getMax() {
        return max;
    }
    public void setMax(final Integer max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
