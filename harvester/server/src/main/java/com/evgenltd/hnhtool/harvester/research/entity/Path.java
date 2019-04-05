package com.evgenltd.hnhtool.harvester.research.entity;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;

import javax.persistence.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 22:25</p>
 */
@Entity
@Table(name = "paths")
public class Path {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private KnownObject from;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private KnownObject to;

    private Double distance;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public KnownObject getFrom() {
        return from;
    }
    public void setFrom(final KnownObject from) {
        this.from = from;
    }

    public KnownObject getTo() {
        return to;
    }
    public void setTo(final KnownObject to) {
        this.to = to;
    }

    public Double getDistance() {
        return distance;
    }
    public void setDistance(final Double distance) {
        this.distance = distance;
    }

}
