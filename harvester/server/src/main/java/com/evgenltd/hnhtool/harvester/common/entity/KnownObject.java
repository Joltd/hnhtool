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

    private Long resourceId;

    private Integer x;

    private Integer y;

    private LocalDateTime actual;

    private Boolean player;

    private Boolean doorway;

    private Boolean container;

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

    public Long getResourceId() {
        return resourceId;
    }
    public void setResourceId(final Long resourceId) {
        this.resourceId = resourceId;
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

    public Boolean getContainer() {
        return container;
    }
    public void setContainer(final Boolean container) {
        this.container = container;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
