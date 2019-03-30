package com.evgenltd.hnhtool.harvester.common.entity;

import javax.persistence.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 23:15</p>
 */
@Entity
@Table(name = "known_items")
public class KnownItem implements Identified {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private KnownObject owner;

    private String name;

    private Double quality;

    private Boolean food;

    private Boolean weapon;

    private Boolean curiosity;

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

    public Boolean getFood() {
        return food;
    }
    public void setFood(final Boolean food) {
        this.food = food;
    }

    public Boolean getWeapon() {
        return weapon;
    }
    public void setWeapon(final Boolean weapon) {
        this.weapon = weapon;
    }

    public Boolean getCuriosity() {
        return curiosity;
    }
    public void setCuriosity(final Boolean curiosity) {
        this.curiosity = curiosity;
    }

}
