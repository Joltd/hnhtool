package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
import com.evgenltd.hnhtool.harvester.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtool.harvester.research.entity.ResearchResultCode;
import com.evgenltd.hnhtools.common.Result;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 27-04-2019 00:35</p>
 */
public class DropItemInWorld {

    private Agent agent;

    private DropItemInWorld() {
        this.agent = TaskContext.getAgent();
    }

    @TaskRequired
    public static Result<Void> perform() {
        return new DropItemInWorld().performImpl();
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
