package com.evgenltd.hnhtool.harvester.common.component;

import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.complexclient.ComplexClient;

/**
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 01-05-2019 14:05</p>
 */
public class TaskContext {

    private static final ThreadLocal<Agent> holder = new ThreadLocal<>();

    public static void setupContext(final Agent agent) {
        holder.set(agent);
    }

    public static void clearContext() {
        holder.remove();
    }

    public static Agent getAgent() {
        final Agent agent = holder.get();
        if (agent == null) {
            throw new ApplicationException("Operation performed outside task context");
        }
        return agent;
    }

    public static ComplexClient getClient() {
        return holder.get().getClient();
    }

}
