package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.Agent;

public abstract class Script {

    private Agent agent;
    private Storekeeper storekeeper;

    public void setAgent(final Agent agent) {
        this.agent = agent;
        storekeeper = new Storekeeper(agent);
    }

    protected Agent getAgent() {
        return agent;
    }

    protected Storekeeper getStorekeeper() {
        return storekeeper;
    }

    public abstract void execute();

}
