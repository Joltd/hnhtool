package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.ComplexClient;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 08-04-2019 21:54</p>
 */
public class OpenContainer {

    private Agent agent;
    private KnownObject container;

    private OpenContainer(final Agent agent, final KnownObject container) {
        this.agent = agent;
        this.container = container;
    }

    public static Result<Void> perform(@NotNull final Agent agent, @NotNull final KnownObject container) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(container, "Container");
        return new OpenContainer(agent, container).performImpl();
    }

    private Result<Void> performImpl() {
        final ComplexClient client = agent.getClient();
        return agent.getMatchedWorldObjectId(container.getId())
                .thenApplyCombine(woId -> client.setParentIdForNewInventory(woId).then(() -> woId))
                .thenApplyCombine(client::interact)
                .thenCombine(() -> Await.performSimple(client::parentIdIsTaken))
                .then(() -> agent.matchItemKnowledge());
    }

}
