package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.CommandUtils;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        final Object previousTargetInventory = agent.getTargetInventory();
        return agent.getMatchedWorldObjectId(container.getId())
                .thenApplyCombine(woId -> agent.getClient().interact(woId))
                .thenCombine(() -> CommandUtils.await(() -> Objects.equals(previousTargetInventory, agent.getTargetInventory())));
    }

}
