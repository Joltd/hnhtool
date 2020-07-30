package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "preferences")
public class Preferences {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    @Type(type = "com.evgenltd.hnhtool.harvester.core.component.type.IntPointType")
    @Columns(columns = {@Column(name = "offsetX"), @Column(name = "offsetY")})
    private IntPoint offset;

    private Integer zoom;

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

    public IntPoint getOffset() {
        return offset;
    }
    public void setOffset(final IntPoint position) {
        this.offset = position;
    }

    public Integer getZoom() {
        return zoom;
    }
    public void setZoom(final Integer zoom) {
        this.zoom = zoom;
    }

}
