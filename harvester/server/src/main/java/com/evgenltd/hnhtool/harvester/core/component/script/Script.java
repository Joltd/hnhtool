package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.Agent;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 25-11-2019 18:29
 */
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
