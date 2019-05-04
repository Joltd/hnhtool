package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
import com.evgenltd.hnhtool.harvester.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
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
public class TakeItemFromStack {

    private Agent agent;
    private KnownObject knownStack;

    private TakeItemFromStack(final KnownObject knownStack) {
        this.agent = TaskContext.getAgent();
        this.knownStack = knownStack;
    }

    @TaskRequired
    public static Result<Void> perform(@NotNull final KnownObject knownStack) {
        Assert.valueRequireNonEmpty(knownStack, "KnownStack");
        return new TakeItemFromStack(knownStack).performImpl();
    }

    private Result<Void> performImpl() {
        return agent.getMatchedWorldInventoryId(knownStack.getId())
                .then(stackId -> agent.getClient().takeItemInHandFromStack(stackId))
                .then(() -> Await.performSimple(() -> agent.getClient().getHand() != null))
                .cast();
    }

}
