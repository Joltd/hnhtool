package com.evgenltd.hnhtool.harvester.common.entity;

import javax.persistence.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 22:54</p>
 */
@Entity
@Table(name = "spaces")
public class Space implements Identified {

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

    enum Type {
        SURFACE,
        MINE,
        HOLE,
        BUILDING
    }

}
