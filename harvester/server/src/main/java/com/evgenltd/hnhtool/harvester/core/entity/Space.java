package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;

@Entity
@Table(name = "spaces")
public class Space {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Type type;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }
    public void setType(final Type type) {
        this.type = type;
    }

    public enum Type {
        SURFACE,
        MINE,
        HOLE,
        BUILDING
    }

}
