package com.evgenltd.hnhtool.harvester_new;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 25-11-2019 18:29
 */
public abstract class Script {

    private final Agent agent;

    public Script(final Agent agent) {
        this.agent = agent;
    }

    protected Agent getAgent() {
        return agent;
    }

    public abstract void execute();

}
