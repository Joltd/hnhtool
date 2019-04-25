package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.ComplexClient;
import org.jetbrains.annotations.NotNull;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 25-04-2019 12:21
 */
public class OpenStack {

    private Agent agent;
    private KnownObject stack;

    private OpenStack(final Agent agent, final KnownObject stack) {
        this.agent = agent;
        this.stack = stack;
    }

    public static Result<Void> perform(@NotNull final Agent agent, @NotNull final KnownObject stack) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(stack, "Stack");
        return new OpenStack(agent, stack).performImpl();
    }

    private Result<Void> performImpl() {
        final ComplexClient client = agent.getClient();
        return agent.getMatchedWorldObjectId(stack.getId())
                .then(client::setParentIdForNewInventory)
                .thenApply(client::interact)
                .thenCombine(() -> Await.performSimple(client::parentIdIsTaken));
    }

}
