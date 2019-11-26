package com.evgenltd.hnhtool.harvester_old.research.command;

import com.evgenltd.hnhtool.harvester_old.common.command.Await;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester_old.common.service.Agent;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-04-2019 23:31</p>
 */
public class AwaitForItem {

    private Agent agent;
    private Long knownItemId;
    private Integer worldItemId;

    private AwaitForItem(final Long knownItemId, final Integer worldItemId) {
        this.agent = TaskContext.getAgent();
        this.knownItemId = knownItemId;
        this.worldItemId = worldItemId;
    }

    @TaskRequired
    public static Result<Void> appear(@NotNull final Long knownItemId) {
        Assert.valueRequireNonEmpty(knownItemId, "KnownItemId");
        return new AwaitForItem(knownItemId, null).appearImpl();
    }

    @TaskRequired
    public static Result<Void> disappear(@NotNull final Integer worldItemId) {
        Assert.valueRequireNonEmpty(worldItemId, "WorldItemId");
        return new AwaitForItem(null, worldItemId).disappearImpl();
    }

    private Result<Void> appearImpl() {
        return Await.simple(() -> {
            final boolean appeared = agent.getMatchedWorldItemId(knownItemId).isSuccess();
            if (appeared) {
                return true;
            }

            agent.matchItemKnowledge();
            return false;
        }).timeout(100L).perform();
    }

    private Result<Void> disappearImpl() {
        return Await.simple(() -> {
            final boolean disappeared = agent.getMatchedKnownItemId(worldItemId).isFailed();
            if (disappeared) {
                return true;
            }

            agent.matchItemKnowledge();
            return false;
        }).timeout(100L).perform();
    }
}
