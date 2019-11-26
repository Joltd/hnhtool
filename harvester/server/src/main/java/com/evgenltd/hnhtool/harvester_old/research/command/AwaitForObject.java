package com.evgenltd.hnhtool.harvester_old.research.command;

import com.evgenltd.hnhtool.harvester_old.common.command.Await;
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
 * <p>Created: 07-05-2019 21:51</p>
 */
public class AwaitForObject {

    private Agent agent;
    private Long knownObjectId;

    private AwaitForObject(final Long knownObjectId) {
        this.agent = TaskContext.getAgent();
        this.knownObjectId = knownObjectId;
    }

    public static Result<Void> disappear(@NotNull final Long knownObjectId) {
        Assert.valueRequireNonEmpty(knownObjectId, "KnownObjectId");
        return new AwaitForObject(knownObjectId).disappearImpl();
    }

    private Result<Void> disappearImpl() {
        return Await.simple(() -> {
            final boolean disappeared = agent.getMatchedWorldObjectId(knownObjectId).isFailed();
            if (disappeared) {
                return true;
            }

            agent.matchObjectKnowledge();
            return false;
        }).timeout(100L).perform();
    }

}
