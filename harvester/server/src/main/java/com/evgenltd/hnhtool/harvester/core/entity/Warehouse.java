package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 23-03-2020 22:32</p>
 */
@Entity
@Table(name = "warehouses")
public class Warehouse {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ElementCollection
    @CollectionTable(name = "warehouse_points", joinColumns = @JoinColumn(name = "warehouse_id"))
    @Type(type = "com.evgenltd.hnhtool.harvester.core.component.type.IntPointType")
    @Columns(columns = {@Column(name = "x"), @Column(name = "y")})
    private Set<IntPoint> points = new HashSet<>();

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public Set<IntPoint> getPoints() {
        return points;
    }
    public void setPoints(final Set<IntPoint> points) {
        this.points = points;
    }

}
