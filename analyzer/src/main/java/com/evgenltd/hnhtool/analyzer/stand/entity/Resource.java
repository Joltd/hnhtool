package com.evgenltd.hnhtool.analyzer.stand.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 01-03-2019 00:00</p>
 */
public class Resource {

    private Long id;
    private String name;
    private int version;

    public Resource() {}

    public Resource(final Long id, final String name, final int version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

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

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format(
                "id [%s], name [%s], version [%s]",
                getId(),
                getName(),
                getVersion()
        );
    }
}
