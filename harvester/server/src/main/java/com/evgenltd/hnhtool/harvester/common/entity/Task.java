package com.evgenltd.hnhtool.harvester.common.entity;

import com.evgenltd.hnhtool.harvester.common.service.Agent;

import java.util.function.Predicate;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 01-04-2019 23:11</p>
 */
public class Task {

    private Long id;

    private Work work;

    private Predicate<Agent> requirements;

    private Status status;

    private String failReason;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public Work getWork() {
        return work;
    }
    public void setWork(final Work work) {
        this.work = work;
    }

    public Predicate<Agent> getRequirements() {
        return requirements;
    }
    public void setRequirements(final Predicate<Agent> requirements) {
        this.requirements = requirements;
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
        FAILED;

        public boolean isFinished() {
            return this == DONE || this == REJECTED || this == FAILED;
        }
    }

}
