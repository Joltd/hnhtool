package com.evgenltd.hnhtool.harvester.common.entity;

import com.evgenltd.hnhtools.agent.ComplexClient;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 31-03-2019 00:10</p>
 */
public class Agent {

    private Account account;
    private ComplexClient client;

    public Account getAccount() {
        return account;
    }
    public void setAccount(final Account account) {
        this.account = account;
    }

    public ComplexClient getClient() {
        return client;
    }
    public void setClient(final ComplexClient client) {
        this.client = client;
    }

}
