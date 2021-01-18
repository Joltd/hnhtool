package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_logs")
public class AgentLog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String log;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(final LocalDateTime date) {
        this.date = date;
    }

    public Account getAccount() {
        return account;
    }
    public void setAccount(final Account account) {
        this.account = account;
    }

    public String getLog() {
        return log;
    }
    public void setLog(final String log) {
        this.log = log;
    }

}
