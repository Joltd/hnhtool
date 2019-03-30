package com.evgenltd.hnhtool.harvester.common.entity;

import javax.persistence.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 22:59</p>
 */
@Entity
@Table(name = "known_objects")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class KnownObject implements Identified {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Space owner;

    private Integer x;

    private Integer y;

    private Boolean doorway;

    private Boolean container;

    @ManyToOne
    @JoinColumn(name = "space_from_id")
    private Space spaceFrom;

    @ManyToOne
    @JoinColumn(name = "space_to_id")
    private Space spaceTo;

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

    public Space getSpaceFrom() {
        return spaceFrom;
    }
    public void setSpaceFrom(final Space spaceFrom) {
        this.spaceFrom = spaceFrom;
    }

    public Space getSpaceTo() {
        return spaceTo;
    }
    public void setSpaceTo(final Space spaceTo) {
        this.spaceTo = spaceTo;
    }

}
