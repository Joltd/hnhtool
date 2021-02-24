package com.evgenltd.hnhtool.harvester.core.entity;

import com.evgenltd.hnhtools.common.Assert;

import javax.persistence.*;

@Entity
@Table(name = "agents")
public class Agent {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String username;

    private byte[] token;

    @Column(name = "`character`")
    private String character;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OFFLINE;

    private boolean enabled = true;

    private boolean accident;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        if (Assert.isEmpty(getUsername())) {
            return "None";
        } else if (Assert.isEmpty(getCharacter())) {
            return getUsername();
        } else {
            return String.format("%s (%s)", getUsername(), getCharacter());
        }
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(final String username) {
        this.username = username;
    }

    public byte[] getToken() {
        return token;
    }
    public void setToken(final byte[] token) {
        this.token = token;
    }

    public String getCharacter() {
        return character;
    }
    public void setCharacter(final String characterName) {
        this.character = characterName;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(final Status status) {
        this.status = status;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccident() {
        return accident;
    }
    public void setAccident(final boolean accident) {
        this.accident = accident;
    }

    public String asString() {
        return String.format("%s (%s)", getUsername(), getCharacter());
    }

    public enum Status {
        OFFLINE,
        IDLE,
        IN_PROGRESS,

        NOT_AUTHENTICATED,
        CHARACTER_NOT_SELECTED
    }
}
