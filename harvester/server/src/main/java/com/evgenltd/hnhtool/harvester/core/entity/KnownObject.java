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
    @JoinColumn(name = "space_id")
    private Space space;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private KnownObject parent;

    @Enumerated(EnumType.STRING)
    private Place place;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private LocalDateTime actual;

    private Boolean lost = false;

    private Integer x;

    private Integer y;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public Space getSpace() {
        return space;
    }
    public void setSpace(final Space space) {
        this.space = space;
    }

    public KnownObject getParent() {
        return parent;
    }
    public void setParent(final KnownObject parent) {
        this.parent = parent;
    }

    public Place getPlace() {
        return place;
    }
    public void setPlace(final Place place) {
        this.place = place;
    }

    public Resource getResource() {
        return resource;
    }
    public void setResource(final Resource resource) {
        this.resource = resource;
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

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public enum Place {
        MAIN_INVENTORY,
        STUDY_INVENTORY,
        EQUIP,
        HAND
    }

}
