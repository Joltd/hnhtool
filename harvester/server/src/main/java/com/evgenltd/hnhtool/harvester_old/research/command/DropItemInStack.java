package com.evgenltd.hnhtool.harvester_old.research.command;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester_old.common.command.Await;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester_old.common.service.Agent;
import com.evgenltd.hnhtool.harvester_old.research.entity.ResearchResultCode;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 27-04-2019 00:42</p>
 */
public class DropItemInStack {

    private Agent agent;
    private KnownObject knownStack;

    private DropItemInStack(final KnownObject knownStack) {
        this.agent = TaskContext.getAgent();
        this.knownStack = knownStack;
    }

    @TaskRequired
    public static Result<Void> perform(@NotNull final KnownObject knownStack) {
        Assert.valueRequireNonEmpty(knownStack, "KnownStack");
        return new DropItemInStack(knownStack).performImpl();
    }

    private Result<Void> performImpl() {
        if (agent.getClient().getHand() == null) {
            return Result.fail(ResearchResultCode.HAND_IS_EMPTY);
        }

        return agent.getMatchedWorldInventoryId(knownStack.getId())
                .then(stackId -> agent.getClient().dropItemFromHandInStack(stackId))
                .then(() -> Await.performSimple(() -> agent.getClient().getHand() == null))
                .cast();
    }

}