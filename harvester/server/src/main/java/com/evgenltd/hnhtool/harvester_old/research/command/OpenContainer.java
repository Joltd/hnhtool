package com.evgenltd.hnhtool.harvester_old.research.command;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester_old.common.command.Await;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester_old.common.service.Agent;
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

    private OpenContainer(final KnownObject container) {
        this.agent = TaskContext.getAgent();
        this.container = container;
    }

    @TaskRequired
    public static Result<Void> perform(@NotNull final KnownObject container) {
        Assert.valueRequireNonEmpty(container, "Container");
        return new OpenContainer(container).performImpl();
    }

    private Result<Void> performImpl() {
        final ComplexClient client = agent.getClient();
        return agent.getMatchedWorldObjectId(container.getId())
                .then(client::setParentIdForNewInventory)
                .thenApplyCombine(client::interact)
                .thenCombine(() -> Await.performSimple(client::parentIdIsTaken));
    }

}
