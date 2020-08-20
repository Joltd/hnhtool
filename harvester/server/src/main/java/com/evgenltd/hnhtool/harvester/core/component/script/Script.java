package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.Agent;

public abstract class Script {

    private Agent agent;

    public void setAgent(final Agent agent) {
        this.agent = agent;
    }

    protected Agent getAgent() {
        return agent;
    }

    public abstract void execute();

}
