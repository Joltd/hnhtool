package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;

@Entity
@Table(name = "jobs")
@Inheritance(strategy = InheritanceType.JOINED)
public class Job {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private boolean enabled;

    private String type;

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

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }
    public void setType(final String type) {
        this.type = type;
    }

}
