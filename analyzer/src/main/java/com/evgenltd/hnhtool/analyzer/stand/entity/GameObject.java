package com.evgenltd.hnhtool.analyzer.stand.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 01-03-2019 00:00</p>
 */
public class GameObject {

    private Long id;
    private Integer frame;
    private Resource resource;

    private Long x;
    private Long y;
    private Double angel;

    public GameObject(final Long id, final Integer frame) {
        this.id = id;
        this.frame = frame;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Integer getFrame() {
        return frame;
    }

    public void setFrame(final Integer frame) {
        this.frame = frame;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(final Resource resource) {
        this.resource = resource;
    }

    public Long getX() {
        return x;
    }

    public void setX(final Long x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(final Long y) {
        this.y = y;
    }

    public Double getAngel() {
        return angel;
    }

    public void setAngel(final Double angel) {
        this.angel = angel;
    }

    @Override
    public String toString() {
        return String.format(
                "id [%s], frame [%s], resource [%s], x [%s], y [%s], angel [%s]",
                getId(),
                getFrame(),
                getResource() != null ? getResource().getName() : "null",
                getX(),
                getY(),
                getAngel()
        );
    }
}
