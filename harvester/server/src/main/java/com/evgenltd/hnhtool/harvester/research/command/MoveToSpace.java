package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Resource;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.command.CommandUtils;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.WorldObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

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
    private Space targetSpace;

    private List<Long> worldObjectsSnapshot;

    private MoveToSpace(final Agent agent, final KnownObject doorway, final Space targetSpace) {
        this.agent = agent;
        this.doorway = doorway;
        this.targetSpace = targetSpace;
    }

    public static Result<Void> perform(@NotNull final Agent agent, @NotNull final KnownObject doorwayId, @NotNull final Space targetSpace) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(doorwayId, "DoorwayId");
        Assert.valueRequireNonEmpty(targetSpace, "TargetSpace");
        return new MoveToSpace(agent, doorwayId, targetSpace).performImpl();
    }

    private Result<Void> performImpl() {
        agent.matchKnowledge();
        agent.knowledgeMatchingWithResearch(true);

        worldObjectsSnapshot = takeWorldObjectSnapshot();
        log.info("Matching state {}", worldObjectsSnapshot);

        final Result<Long> matchedDoorwayId = agent.getMatchedWorldObjectId(doorway.getId());
        if (matchedDoorwayId.isFailed()) {
            log.info("Not found target doorwayId=[{}]", doorway);
            return matchedDoorwayId.cast();
        }

        return agent.getClient()
                .interact(matchedDoorwayId.getValue(), decideObjectPartition())
                .thenCombine(() -> CommandUtils.awaitWithResult(this::isAwaitDone))
                .then(() -> agent.changeSpace(targetSpace))
                .anyway(() -> agent.knowledgeMatchingWithResearch(false));
    }

    private Result<Boolean> isAwaitDone() {

        final List<Long> newSnapshot = takeWorldObjectSnapshot();
        final boolean objectsFullyChanged = isSnapshotCompletelyDifferent(worldObjectsSnapshot, newSnapshot);
        return Result.ok(objectsFullyChanged);

//        return Result.fail(ResearchResultCode.SPACE_NOT_REACHED);

    }

    @NotNull
    private List<Long> takeWorldObjectSnapshot() {
        return agent.getClient()
                .getWorldObjects()
                .stream()
                .map(WorldObject::getId)
                .collect(Collectors.toList());
    }

    private boolean isSnapshotCompletelyDifferent(final List<Long> firstSnapshot, final List<Long> secondSnapshot) {
        if (firstSnapshot.size() != secondSnapshot.size()) {
            return true;
        }

        int sameObjectCount = 0;

        for (final Long firstId : firstSnapshot) {
            if (secondSnapshot.contains(firstId)) {
                sameObjectCount++;
            }
        }

        final double similarity = (double) sameObjectCount / (double) firstSnapshot.size();
        return similarity < 0.3;
    }

    private Integer decideObjectPartition() {
        final Resource resource = doorway.getResource();
        if (resource == null) {
            return -1;
        }

        if (ResourceConstants.TIMBER_HOUSE.equals(resource.getName())) {
            return 16;
        } else {
            return -1;
        }
    }

}
