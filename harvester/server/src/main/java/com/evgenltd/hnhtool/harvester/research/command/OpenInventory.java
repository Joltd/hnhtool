package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.command.CommandUtils;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 08-04-2019 21:54</p>
 */
public class OpenInventory {

    private Agent agent;
    private KnownObject container;

    private OpenInventory(final Agent agent, final KnownObject container) {
        this.agent = agent;
        this.container = container;
    }

    public static Result<Inventory> perform(@NotNull final Agent agent, @NotNull final KnownObject container) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(container, "Container");
        return new OpenInventory(agent, container).performImpl();
    }

    private Result<Inventory> performImpl() {
        return agent.getMatchedWorldObjectId(container.getId())
                .thenApplyCombine(woId -> agent.getClient().interact(woId))
                .then(() -> CommandUtils.await(this::isAwaitDone))
                .thenApplyCombine(p -> agent.getClient().getLastOpenedInventory());
    }

    private boolean isAwaitDone() {
        return agent.getClient().hasLastOpenedInventory();
    }

}
