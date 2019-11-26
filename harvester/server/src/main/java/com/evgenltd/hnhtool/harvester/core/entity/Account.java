package com.evgenltd.hnhtool.harvester.core.entity;

import javax.persistence.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 23:36</p>
 */
@Entity
@Table(name = "accounts")
public class Account {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String username;

    private byte[] token;

    private String characterName;

    @Deprecated
    private String defaultCharacter;

    @ManyToOne
    @JoinColumn(name = "character_object_id")
    @Deprecated
    private KnownObject characterObject;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
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

    public String getCharacterName() {
        return characterName;
    }
    public void setCharacterName(final String characterName) {
        this.characterName = characterName;
    }

    public String getDefaultCharacter() {
        return defaultCharacter;
    }
    public void setDefaultCharacter(final String defaultCharacter) {
        this.defaultCharacter = defaultCharacter;
    }

    public KnownObject getCharacterObject() {
        return characterObject;
    }
    public void setCharacterObject(final KnownObject characterObject) {
        this.characterObject = characterObject;
    }
}
