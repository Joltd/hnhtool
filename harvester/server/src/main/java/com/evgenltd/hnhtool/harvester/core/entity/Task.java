package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    private LocalDateTime actual;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String script;

    private String log;

    private String failReason;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public Agent getAgent() {
        return agent;
    }
    public void setAgent(final Agent agent) {
        this.agent = agent;
    }

    public LocalDateTime getActual() {
        return actual;
    }
    public void setActual(final LocalDateTime created) {
        this.actual = created;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(final Status status) {
        this.status = status;
    }

    public String getScript() {
        return script;
    }
    public void setScript(final String script) {
        this.script = script;
    }

    public String getLog() {
        return log;
    }
    public void setLog(final String log) {
        this.log = log;
    }

    public String getFailReason() {
        return failReason;
    }
    public void setFailReason(final String failReason) {
        this.failReason = failReason;
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE,
        FAILED
    }

}
