package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "warehouse_cells")
public class WarehouseCell {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Type(type = "com.evgenltd.hnhtool.harvester.core.component.type.IntPointType")
    @Columns(columns = {@Column(name = "x"), @Column(name = "y")})
    private IntPoint position;

    @ManyToOne
    @JoinColumn(name = "container_id")
    private KnownObject container;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }
    public void setWarehouse(final Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public IntPoint getPosition() {
        return position;
    }
    public void setPosition(final IntPoint position) {
        this.position = position;
    }

    public KnownObject getContainer() {
        return container;
    }
    public void setContainer(final KnownObject container) {
        this.container = container;
    }

}
