package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 03-12-2019 23:58</p>
 */
@Entity
@Table(name = "resource_groups")
public class ResourceGroup {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @OneToMany(mappedBy = "group")
    private Set<Resource> resources = new HashSet<>();

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public Set<Resource> getResources() {
        return resources;
    }
    public void setResources(final Set<Resource> resources) {
        this.resources = resources;
    }

}
