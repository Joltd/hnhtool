package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.service.Storekeeper;

public abstract class Script {

    private Agent agent;
    private Storekeeper storekeeper;

    protected Agent getAgent() {
        return agent;
    }

    public void setAgent(final Agent agent) {
        this.agent = agent;
    }

    protected Storekeeper getStorekeeper() {
        return storekeeper;
    }

    public void setStorekeeper(final Storekeeper storekeeper) {
        this.storekeeper = storekeeper;
    }

    public abstract void execute();

}
