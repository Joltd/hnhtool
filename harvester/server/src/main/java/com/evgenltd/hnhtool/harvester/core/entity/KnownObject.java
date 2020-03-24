package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "parent")
    private Set<KnownObject> children = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Place place;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private LocalDateTime actual;

    private Boolean lost = false;

    @Type(type = "com.evgenltd.hnhtool.harvester.core.component.type.IntPointType")
    @Columns(columns = {@Column(name = "x"), @Column(name = "y")})
    private IntPoint position;

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

    public Set<KnownObject> getChildren() {
        return children;
    }
    public void setChildren(final Set<KnownObject> childes) {
        this.children = childes;
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

    public IntPoint getPosition() {
        return position;
    }
    public void setPosition(final IntPoint position) {
        this.position = position;
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
