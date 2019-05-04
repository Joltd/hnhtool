package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtool.harvester.research.entity.ResearchResultCode;
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

    private DropItemInStack(final Agent agent, final KnownObject knownStack) {
        this.agent = agent;
        this.knownStack = knownStack;
    }

    public static Result<Void> perform(@NotNull final Agent agent, @NotNull final KnownObject knownStack) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(knownStack, "KnownStack");
        return new DropItemInStack(agent, knownStack).performImpl();
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
