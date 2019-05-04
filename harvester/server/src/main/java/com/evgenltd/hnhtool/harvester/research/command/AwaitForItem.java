package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
import com.evgenltd.hnhtool.harvester.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.common.Assert;
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
    public static void appear(@NotNull final Long knownItemId) {
        Assert.valueRequireNonEmpty(knownItemId, "KnownItemId");
        new AwaitForItem(knownItemId, null).appearImpl();
    }

    @TaskRequired
    public static void disappear(@NotNull final Integer worldItemId) {
        Assert.valueRequireNonEmpty(worldItemId, "WorldItemId");
        new AwaitForItem(null, worldItemId).disappearImpl();
    }

    private void appearImpl() {
        Await.simple(() -> {
            final boolean appeared = agent.getMatchedWorldItemId(knownItemId).isSuccess();
            if (appeared) {
                return true;
            }

            agent.matchItemKnowledge();
            return false;
        }).timeout(100L).perform();
    }

    private void disappearImpl() {
        Await.simple(() -> {
            final boolean disappeared = agent.getMatchedKnownItemId(worldItemId).isFailed();
            if (disappeared) {
                return true;
            }

            agent.matchItemKnowledge();
            return false;
        }).timeout(100L).perform();
    }
}
