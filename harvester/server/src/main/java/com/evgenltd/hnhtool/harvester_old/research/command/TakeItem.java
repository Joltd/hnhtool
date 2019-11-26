package com.evgenltd.hnhtool.harvester_old.research.command;

import com.evgenltd.hnhtool.harvester.core.entity.KnownItem;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester_old.common.service.Agent;
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
        return AwaitForItem.appear(knownItem.getId())
                .thenCombine(() -> agent.getMatchedWorldItemId(knownItem.getId()))
                .then(woId -> agent.getClient().takeItemInHand(woId))
                .thenApplyCombine(AwaitForItem::disappear)
                .cast();
    }

}
