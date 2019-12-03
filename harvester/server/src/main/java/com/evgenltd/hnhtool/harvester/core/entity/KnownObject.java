package com.evgenltd.hnhtool.harvester.core.entity;

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
public class KnownObject {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Space owner;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private Integer x;

    private Integer y;

    private LocalDateTime actual;

    private Boolean lost = false;

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

    public Resource getResource() {
        return resource;
    }
    public void setResource(final Resource resource) {
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

    public Boolean getLost() {
        return lost;
    }
    public void setLost(final Boolean lost) {
        this.lost = lost;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
