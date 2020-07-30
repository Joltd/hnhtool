package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;

@Entity
@Table(name = "resource_contents")
public class ResourceContent {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Lob
    private byte[] data;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }
    public void setData(final byte[] data) {
        this.data = data;
    }

}
