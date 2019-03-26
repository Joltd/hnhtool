package com.evgenltd.hnhtool.harvester.security.entity;

import javax.persistence.*;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 26-03-2019 14:43
 */
@Entity
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

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

    public String getPassword() {
        return password;
    }
    public void setPassword(final String password) {
        this.password = password;
    }

}
