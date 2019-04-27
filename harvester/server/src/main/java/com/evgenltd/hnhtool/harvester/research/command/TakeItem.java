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
 * <p>Created: 27-04-2019 00:20</p>
 */
public class TakeItem {

    private Agent agent;
    private KnownItem knownItem;

    private TakeItem(final Agent agent, final KnownItem knownItem) {
        this.agent = agent;
        this.knownItem = knownItem;
    }

    public static Result<Void> perform(@NotNull final Agent agent, @NotNull final KnownItem knownItem) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(knownItem, "KnownItem");
        return new TakeItem(agent, knownItem).performImpl();
    }

    private Result<Void> performImpl() {
        AwaitForItem.appear(agent, knownItem.getId());
        return agent.getMatchedWorldItemId(knownItem.getId())
                .then(woId -> agent.getClient().takeItemInHand(woId))
                .then(woId -> AwaitForItem.disappear(agent, woId))
                .cast();
    }

}
