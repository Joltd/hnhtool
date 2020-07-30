package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "warehouses")
public class Warehouse {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    @OneToMany(mappedBy = "warehouse")
    private Set<WarehouseCell> cells = new HashSet<>();

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

    public Set<WarehouseCell> getCells() {
        return cells;
    }
    public void setCells(final Set<WarehouseCell> cells) {
        this.cells = cells;
    }

}
