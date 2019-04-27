package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
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
 * <p>Created: 27-04-2019 00:35</p>
 */
public class DropItemInWorld {

    private Agent agent;

    private DropItemInWorld(final Agent agent) {
        this.agent = agent;
    }

    public static Result<Void> perform(@NotNull final Agent agent) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        return new DropItemInWorld(agent).performImpl();
    }

    private Result<Void> performImpl() {
        if (agent.getClient().getHand() == null) {
            return Result.fail(ResearchResultCode.HAND_IS_EMPTY);
        }

        return agent.getClient().dropItemFromHandInWorld()
                .then(() -> Await.performSimple(() -> agent.getClient().getHand() != null))
                .cast();
    }

}
