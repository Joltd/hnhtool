package com.evgenltd.hnhtool.harvester_old.research.command;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester_old.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester_old.common.command.Await;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester_old.common.service.Agent;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-04-2019 12:15</p>
 */
public class MoveToSpace {

    private static final Logger log = LogManager.getLogger(MoveToSpace.class);

    private Agent agent;
    private KnownObject doorway;

    private Space originSpace;

    private MoveToSpace(final KnownObject doorway) {
        this.agent = TaskContext.getAgent();
        this.doorway = doorway;
    }

    @TaskRequired
    public static Result<Void> perform(@NotNull final KnownObject doorwayId) {
        Assert.valueRequireNonEmpty(doorwayId, "DoorwayId");
        return new MoveToSpace(doorwayId).performImpl();
    }

    private Result<Void> performImpl() {
        originSpace = agent.getCurrentSpace();
        log.info("Current complexclient space {}", originSpace.getId());

        agent.knowledgeMatchingWithResearch(true);

        final Result<Long> matchedDoorwayId = agent.getMatchedWorldObjectId(doorway.getId());
        if (matchedDoorwayId.isFailed()) {
            log.info("Not found target doorwayId=[{}]", doorway);
            return matchedDoorwayId.cast();
        }

        return agent.getClient()
                .interact(matchedDoorwayId.getValue(), decideObjectPartition())
                .thenCombine(() -> Await.perform(this::isAwaitDone))
                .anyway(() -> agent.knowledgeMatchingWithResearch(false));
    }

    private Result<Boolean> isAwaitDone() {
        final Space currentSpace = agent.getCurrentSpace();
        return Result.ok(!Objects.equals(originSpace.getId(), currentSpace.getId()));
    }

    private Integer decideObjectPartition() {
        final String resource = doorway.getResource();
        if (resource == null) {
            return -1;
        }

        if (ResourceConstants.TIMBER_HOUSE.equals(resource)) {
            return 16;
        } else {
            return -1;
        }
    }

}