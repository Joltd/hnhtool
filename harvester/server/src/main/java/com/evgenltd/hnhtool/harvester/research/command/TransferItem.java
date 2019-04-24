package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.entity.KnownItem;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 17-04-2019 23:39</p>
 */
public class TransferItem {

    private Agent agent;
    private KnownItem knownItem;

    private TransferItem(final Agent agent, final KnownItem knownItem) {
        this.agent = agent;
        this.knownItem = knownItem;
    }

    public static Result<Void> perform(@NotNull final Agent agent, @NotNull final KnownItem knownItem) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(knownItem, "KnownItem");
        return new TransferItem(agent, knownItem).performImpl();
    }

    private Result<Void> performImpl() {
        AwaitForItem.appear(agent, knownItem.getId());
        return agent.getMatchedWorldItemId(knownItem.getId())
                .then(woId -> agent.getClient().transferItem(woId))
                .then(woId -> AwaitForItem.disappear(agent, woId))
                .cast();
    }

}
