package com.evgenltd.hnhtool.harvester.common.entity;

import com.evgenltd.hnhtool.harvester.common.component.ObjectIndex;
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
    private Space currentSpace;
    private ObjectIndex index;

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

    public Space getCurrentSpace() {
        return currentSpace;
    }
    public void setCurrentSpace(final Space currentSpace) {
        this.currentSpace = currentSpace;
    }

    public ObjectIndex getIndex() {
        return index;
    }
    public void setIndex(final ObjectIndex index) {
        this.index = index;
    }
}
