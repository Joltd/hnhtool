package com.evgenltd.hnhtool.harvester.security.entity;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 26-03-2019 19:04
 */
public class Credentials {

    private String username;
    private String password;

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
