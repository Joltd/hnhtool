package com.evgenltd.hnhtools.agent;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 14:26</p>
 */
public final class Item {

    private Integer id;
    private Integer parentId;
    private String name;
    private float quality;

    public Item(final Integer id, final Integer parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    public Integer getId() {
        return id;
    }

    public Integer getParentId() {
        return parentId;
    }
}
