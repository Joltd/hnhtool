package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 03-12-2019 00:38</p>
 */
@Entity
@Table(name = "resources")
public class Resource {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ResourceGroup group;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "content_id")
    private ResourceContent content;

    private String name;

    private Boolean unknown = false;

    private boolean player = false;

    private boolean prop = false;

    private boolean box = false;

    private boolean heap = false;

    private boolean item = false;

    @Type(type = "com.evgenltd.hnhtool.harvester.core.component.type.IntPointType")
    @Columns(columns = {@Column(name = "x"), @Column(name = "y")})
    private IntPoint size = new IntPoint(0,0);

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public ResourceGroup getGroup() {
        return group;
    }
    public void setGroup(final ResourceGroup group) {
        this.group = group;
    }

    public ResourceContent getContent() {
        return content;
    }
    public void setContent(final ResourceContent content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getUnknown() {
        return unknown;
    }
    public void setUnknown(final Boolean unknown) {
        this.unknown = unknown;
    }

    public boolean isPlayer() {
        return player;
    }
    public void setPlayer(final boolean player) {
        this.player = player;
    }

    public boolean isProp() {
        return prop;
    }
    public void setProp(final boolean prop) {
        this.prop = prop;
    }

    public boolean isBox() {
        return box;
    }
    public void setBox(final boolean container) {
        this.box = container;
    }

    public boolean isHeap() {
        return heap;
    }
    public void setHeap(final boolean heap) {
        this.heap = heap;
    }

    public boolean isItem() {
        return item;
    }
    public void setItem(final boolean item) {
        this.item = item;
    }

    public IntPoint getSize() {
        return size;
    }
    public void setSize(final IntPoint size) {
        this.size = size;
    }
}
