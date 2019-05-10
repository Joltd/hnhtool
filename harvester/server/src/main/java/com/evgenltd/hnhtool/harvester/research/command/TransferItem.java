package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester.common.component.TaskRequired;
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

    private TransferItem(final KnownItem knownItem) {
        this.agent = TaskContext.getAgent();
        this.knownItem = knownItem;
    }

    @TaskRequired
    public static Result<Void> perform(@NotNull final KnownItem knownItem) {
        Assert.valueRequireNonEmpty(knownItem, "KnownItem");
        return new TransferItem(knownItem).performImpl();
    }

    private Result<Void> performImpl() {
        return AwaitForItem.appear(knownItem.getId())
                .thenCombine(() -> agent.getMatchedWorldItemId(knownItem.getId()))
                .then(woId -> agent.getClient().transferItem(woId))
                .thenApplyCombine(AwaitForItem::disappear)
                .cast();
    }

}
