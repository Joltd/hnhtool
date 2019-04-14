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

    private static final String RESEARCH_DOORWAY = "RESEARCH_DOORWAY";
    private static final String RESEARCH_CONTAINER = "RESEARCH_CONTAINER";
    
    private SpaceRepository spaceRepository;
    private KnownObjectRepository knownObjectRepository;
    private TaskRepository taskRepository;
    private PathRepository pathRepository;
    private RoutingService routingService;

    private Task researchDoorwayTask;
    private KnownObject targetDoorway;
    private Space targetSpace;

    private Task researchContainerTask;
    private KnownObject targetContainer;
    
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
        scheduleDoorwayResearch();
//        scheduleContainerResearch();
    }

    @Override
    @NotNull
    public Work getTaskWork(final String step) {
        return this::researchDoorway;
    }

    // ##################################################
    // #                                                #
    // #  Doorway Research                              #
    // #                                                #
    // ##################################################

    private void scheduleDoorwayResearch() {
        if (isResearchDoorwayTaskExists()) {
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

        researchDoorwayTask = taskRepository.openTask(getClass(), RESEARCH_DOORWAY);
        log.info("Created research task, id=[{}]", researchDoorwayTask.getId());
    }


    private boolean isResearchDoorwayTaskExists() {
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

    private Result<Void> researchDoorway(final Agent agent) {
        return routingService.route(agent.getCharacter(), targetDoorway)
                .thenApplyCombine(route -> moveToDoorwayByRoute(agent, route))
                .thenCombine(() -> MoveToSpace.perform(agent, targetDoorway, targetSpace))
                .then(agent::matchKnowledge)
                .thenApplyCombine(p -> agent.getClient().getCharacterPosition())
                .thenApplyCombine(this::storeDoorwayResearchResult)
                .whenFail(this::cleanupOnFailed);
    }

    private Result<Void> moveToDoorwayByRoute(final Agent agent, final List<KnownObject> route) {
        route.remove(agent.getCharacter());
        route.remove(targetDoorway);
        return MoveByRoute.perform(agent, route);
    }

    private Result<Void> storeDoorwayResearchResult(final IntPoint characterPosition) {
        final List<KnownObject> nearestDoorways = knownObjectRepository.findNearestDoorway(
                targetSpace,
                characterPosition.getX(),
                characterPosition.getY()
        );
        if (nearestDoorways.isEmpty()) {
            return Result.fail(ResearchResultCode.NEAREST_DOORWAY_IN_SPACE_NOT_FOUND);
        }

        final KnownObject nearestDoorway = nearestDoorways.get(0);
        log.info("Nearest door after move to space id=[{}]", nearestDoorway.getId());
        
        knownObjectRepository.markAsResearched(targetDoorway);
        knownObjectRepository.markAsResearched(nearestDoorway);

        final Path path = new Path();
        path.setFrom(targetDoorway);
        path.setTo(nearestDoorway);
        path.setDistance(0D);
        pathRepository.save(path);

        researchDoorwayTask = null;

        return Result.ok();
    }

    private void cleanupOnFailed() {
        spaceRepository.delete(targetSpace);
    }

    // ##################################################
    // #                                                #
    // #  Container Research                            #
    // #                                                #
    // ##################################################

    private void scheduleContainerResearch() {
        if (isResearchContainerTaskExists()) {
            return;
        }
        
        final List<KnownObject> unknownContainer = knownObjectRepository.findUnknownContainers();
        if (unknownContainer.isEmpty()) {
            return;
        }
        
        targetContainer = unknownContainer.get(0);
        log.info("Found unknown container, id=[{}], owner=[{}]", targetContainer.getId(), targetContainer.getOwner().getId());
        
        researchContainerTask = taskRepository.openTask(getClass(), RESEARCH_CONTAINER);
        log.info("Created research task, id=[{}]", researchContainerTask.getId());
    }

    private boolean isResearchContainerTaskExists() {
        if (researchContainerTask == null) {
            return false;
        }
        return taskRepository.findById(researchContainerTask.getId())
                .map(task -> {
                    researchContainerTask = task;
                    return true;
                })
                .orElse(false);
    }
    
    private void researchContainer(final Agent agent) {

    }

    private Result<Void> moveToContainerByRoute(final Agent agent, final List<KnownObject> route) {
        route.remove(agent.getCharacter());
        route.remove(targetContainer);
        return MoveByRoute.perform(agent, route);
    }


}
