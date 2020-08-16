package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.Agent;

public abstract class Script {

    private Agent agent;
    private Storekeeper storekeeper;
    private Routing routing;

    public void setAgent(final Agent agent) {
        this.agent = agent;
        this.storekeeper = new Storekeeper(agent);
        this.routing = new Routing(agent);
    }

    protected Agent getAgent() {
        return agent;
    }

    protected Storekeeper getStorekeeper() {
        return storekeeper;
    }

    protected Routing getRouting() {
        return routing;
    }

    public abstract void execute();

}
