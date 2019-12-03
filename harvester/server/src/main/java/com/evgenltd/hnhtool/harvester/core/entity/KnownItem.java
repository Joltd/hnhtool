package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 23:15</p>
 */
@Entity
@Table(name = "known_items")
public class KnownItem {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private KnownObject owner;

    @Enumerated(EnumType.STRING)
    private Place place;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private Integer x;

    private Integer y;

    private LocalDateTime actual;

    private Boolean lost = false;

    private String name;

    private Double quality;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public KnownObject getOwner() {
        return owner;
    }
    public void setOwner(final KnownObject owner) {
        this.owner = owner;
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

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public Double getQuality() {
        return quality;
    }
    public void setQuality(final Double quality) {
        this.quality = quality;
    }

    public enum Place {
        MAIN_INVENTORY,
        STUDY_INVENTORY,
        EQUIP,
        HAND
    }

}
