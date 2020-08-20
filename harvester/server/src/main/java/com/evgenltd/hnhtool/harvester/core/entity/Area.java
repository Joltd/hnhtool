package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "areas")
public class Area {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    @Type(type = "com.evgenltd.hnhtool.harvester.core.component.type.IntPointType")
    @Columns(columns = {@Column(name = "fromX"), @Column(name = "fromY")})
    private IntPoint from;

    @Type(type = "com.evgenltd.hnhtool.harvester.core.component.type.IntPointType")
    @Columns(columns = {@Column(name = "toX"), @Column(name = "toY")})
    private IntPoint to;

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

    public IntPoint getFrom() {
        return from;
    }
    public void setFrom(final IntPoint from) {
        this.from = from;
    }

    public IntPoint getTo() {
        return to;
    }
    public void setTo(final IntPoint to) {
        this.to = to;
    }
}
