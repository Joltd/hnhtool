package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.component.TaskContext;
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

    private TakeItem(final KnownItem knownItem) {
        this.agent = TaskContext.getAgent();
        this.knownItem = knownItem;
    }

    public static Result<Void> perform(@NotNull final KnownItem knownItem) {
        Assert.valueRequireNonEmpty(knownItem, "KnownItem");
        return new TakeItem(knownItem).performImpl();
    }

    private Result<Void> performImpl() {
        AwaitForItem.appear(knownItem.getId());
        return agent.getMatchedWorldItemId(knownItem.getId())
                .then(woId -> agent.getClient().takeItemInHand(woId))
                .then(AwaitForItem::disappear)
                .cast();
    }

}
