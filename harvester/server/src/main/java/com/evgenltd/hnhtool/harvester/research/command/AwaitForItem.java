package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
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

    private AwaitForItem(final Agent agent, final Long knownItemId, final Integer worldItemId) {
        this.agent = agent;
        this.knownItemId = knownItemId;
        this.worldItemId = worldItemId;
    }

    public static void appear(@NotNull final Agent agent, @NotNull final Long knownItemId) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(knownItemId, "KnownItemId");
        new AwaitForItem(agent, knownItemId, null).appearImpl();
    }

    public static void disappear(@NotNull final Agent agent, @NotNull final Integer worldItemId) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(worldItemId, "WorldItemId");
        new AwaitForItem(agent, null, worldItemId).disappearImpl();
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
