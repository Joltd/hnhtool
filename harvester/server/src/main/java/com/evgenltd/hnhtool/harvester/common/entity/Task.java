package com.evgenltd.hnhtool.harvester.common.entity;

import javax.persistence.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 01-04-2019 23:11</p>
 */
@Entity
@Table(name = "tasks")
public class Task {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String module;

    private String step;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String failReason;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }
    public void setModule(final String module) {
        this.module = module;
    }

    public String getStep() {
        return step;
    }
    public void setStep(final String step) {
        this.step = step;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(final Status status) {
        this.status = status;
    }

    public String getFailReason() {
        return failReason;
    }
    public void setFailReason(final String failReason) {
        this.failReason = failReason;
    }

    public enum Status {
        OPEN,
        IN_PROGRESS,
        DONE,
        REJECTED,
        FAILED
    }

}
