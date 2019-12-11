package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-12-2019 00:00</p>
 */
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
