package com.evgenltd.hnhtool.harvester.research.service;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.common.entity.Task;
import com.evgenltd.hnhtool.harvester.common.entity.Work;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.common.repository.SpaceRepository;
import com.evgenltd.hnhtool.harvester.common.repository.TaskRepository;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtool.harvester.common.service.Module;
import com.evgenltd.hnhtool.harvester.research.command.MoveByRoute;
import com.evgenltd.hnhtool.harvester.research.command.MoveToSpace;
import com.evgenltd.hnhtool.harvester.research.entity.Path;
import com.evgenltd.hnhtool.harvester.research.entity.ResearchResultCode;
import com.evgenltd.hnhtool.harvester.research.repository.PathRepository;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.evgenltd.hnhtool.harvester.common.ResourceConstants.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 00:57</p>
 */
@Service
public class ResearchService implements Module {

    private static final Logger log = LogManager.getLogger(ResearchService.class);

    private SpaceRepository spaceRepository;
    private KnownObjectRepository knownObjectRepository;
    private TaskRepository taskRepository;
    private PathRepository pathRepository;
    private RoutingService routingService;

    private Task researchDoorwayTask;
    private KnownObject targetDoorway;
    private Space targetSpace;

    public ResearchService(
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository,
            final TaskRepository taskRepository,
            final PathRepository pathRepository,
            final RoutingService routingService
    ) {
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
        this.taskRepository = taskRepository;
        this.pathRepository = pathRepository;
        this.routingService = routingService;
    }

    @Scheduled(fixedDelay = 10_000L)
    public void main() {
        if (isResearchDoorwayExists()) {
            return;
        }

        final List<KnownObject> unknownDoorway = knownObjectRepository.findUnknownDoorways();
        if (unknownDoorway.isEmpty()) {
            return;
        }

        targetDoorway = unknownDoorway.get(0);
        log.info("Found unknown door, id=[{}], owner=[{}]", targetDoorway.getId(), targetDoorway.getOwner().getId());

        final Long resourceId = targetDoorway.getResourceId();
        targetSpace = buildNewSpace(resourceId);
        log.info("Created space for other side, id=[{}], type=[{}]", targetSpace.getId(), targetSpace.getType());

        researchDoorwayTask = taskRepository.openTask(getClass(), "DUMMY");
        log.info("Created research task, id=[{}]", researchDoorwayTask.getId());
    }

    @Override
    @NotNull
    public Work getTaskWork(final String step) {
        return this::researchDoorway;
    }

    private Result<Void> researchDoorway(final Agent agent) {
        return routingService.route(agent.getCharacter(), targetDoorway)
                .then(route -> moveByRoute(agent, route))
                .then(moveResult -> MoveToSpace.perform(agent, targetDoorway, targetSpace))
                .then(agent::matchKnowledge)
                .then(p -> agent.getClient().getCharacterPosition())
                .then(this::storeDoorwayResearchResult)
                .whenFailed(this::cleanupOnFailed);
    }

    private Result<Void> moveByRoute(final Agent agent, final List<KnownObject> route) {
        route.remove(agent.getCharacter());
        route.remove(targetDoorway);
        return MoveByRoute.perform(agent, route);
    }

    private Result<Void> storeDoorwayResearchResult(final IntPoint characterPosition) {
        final List<KnownObject> nearestDoorway = knownObjectRepository.findNearestDoorway(
                targetSpace,
                characterPosition.getX(),
                characterPosition.getY()
        );
        if (nearestDoorway.isEmpty()) {
            return Result.fail(ResearchResultCode.NEAREST_DOORWAY_IN_SPACE_NOT_FOUND);
        }

        log.info("Nearest door after move to space id=[{}]", nearestDoorway.get(0).getId());

        final Path path = new Path();
        path.setFrom(targetDoorway);
        path.setTo(nearestDoorway.get(0));
        path.setDistance(0D);
        pathRepository.save(path);

        researchDoorwayTask = null;

        return Result.ok();
    }

    private void cleanupOnFailed() {
        spaceRepository.delete(targetSpace);
    }

    private void researchContainer() {

    }

    private Space buildNewSpace(final Long resourceId) {
        final Space space = new Space();
        if (isDoorwayToBuilding(resourceId)) {
            space.setType(Space.Type.BUILDING);
        } else if (isDoorwayToHole(resourceId)) {
            space.setType(Space.Type.HOLE);
        } else if (isDoorwayToMine(resourceId)) {
            space.setType(Space.Type.MINE);
        } else {
            space.setType(Space.Type.SURFACE);
        }
        space.setName(space.getType().name());
        return spaceRepository.save(space);
    }

    private boolean isResearchDoorwayExists() {
        if (researchDoorwayTask == null) {
            return false;
        }
        return taskRepository.findById(researchDoorwayTask.getId())
                .map(task -> {
                    researchDoorwayTask = task;
                    return true;
                })
                .orElse(false);
    }

}
