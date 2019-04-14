package com.evgenltd.hnhtool.harvester.research.service;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Task;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtool.harvester.common.service.Module;
import com.evgenltd.hnhtool.harvester.common.service.TaskService;
import com.evgenltd.hnhtool.harvester.research.command.MoveByRoute;
import com.evgenltd.hnhtool.harvester.research.command.MoveToSpace;
import com.evgenltd.hnhtool.harvester.research.entity.Path;
import com.evgenltd.hnhtool.harvester.research.entity.ResearchResultCode;
import com.evgenltd.hnhtool.harvester.research.repository.PathRepository;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private KnownObjectRepository knownObjectRepository;
    private TaskService taskService;
    private PathRepository pathRepository;
    private RoutingService routingService;

    private Task researchDoorwayTask;

    private Task researchContainerTask;

    public ResearchService(
            final KnownObjectRepository knownObjectRepository,
            final TaskService taskService,
            final PathRepository pathRepository,
            final RoutingService routingService
    ) {
        this.knownObjectRepository = knownObjectRepository;
        this.taskService = taskService;
        this.pathRepository = pathRepository;
        this.routingService = routingService;
    }

    @Scheduled(fixedDelay = 10_000L)
    public void main() {
        scheduleDoorwayResearch();
//        scheduleContainerResearch();
    }

    // ##################################################
    // #                                                #
    // #  Doorway Research                              #
    // #                                                #
    // ##################################################

    private void scheduleDoorwayResearch() {
        if (researchDoorwayTask != null && !researchDoorwayTask.getStatus().isFinished()) {
            return;
        }

        final List<KnownObject> unknownDoorway = knownObjectRepository.findUnknownDoorways();
        if (unknownDoorway.isEmpty()) {
            return;
        }

        final KnownObject targetDoorway = unknownDoorway.get(0);
        log.info("Found unknown door, id=[{}], owner=[{}]", targetDoorway.getId(), targetDoorway.getOwner().getId());

        researchDoorwayTask = taskService.openTask(agent -> researchDoorway(agent, targetDoorway));
        log.info("Created research task, id=[{}]", researchDoorwayTask.getId());
    }

    private Result<Void> researchDoorway(final Agent agent, final KnownObject targetDoorway) {
        return routingService.route(agent.getCharacter(), targetDoorway)
                .thenApplyCombine(route -> moveToDoorwayByRoute(agent, route, targetDoorway))
                .thenCombine(() -> MoveToSpace.perform(agent, targetDoorway))
                .thenApplyCombine(characterPosition -> storeDoorwayResearchResult(agent, targetDoorway));
    }

    private Result<Void> moveToDoorwayByRoute(
            final Agent agent,
            final List<KnownObject> route,
            final KnownObject targetDoorway
    ) {
        route.remove(agent.getCharacter());
        route.remove(targetDoorway);
        return MoveByRoute.perform(agent, route);
    }

    private Result<Void> storeDoorwayResearchResult(
            final Agent agent,
            final KnownObject targetDoorway
    ) {
        final IntPoint characterPosition = agent.getCharacter().getPosition();
        final List<KnownObject> nearestDoorways = knownObjectRepository.findNearestDoorway(
                agent.getCurrentSpace(),
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

    // ##################################################
    // #                                                #
    // #  Container Research                            #
    // #                                                #
    // ##################################################

    private void scheduleContainerResearch() {
        if (researchContainerTask != null && !researchContainerTask.getStatus().isFinished()) {
            return;
        }
        
        final List<KnownObject> unknownContainer = knownObjectRepository.findUnknownContainers();
        if (unknownContainer.isEmpty()) {
            return;
        }
        
        final KnownObject targetContainer = unknownContainer.get(0);
        log.info("Found unknown container, id=[{}], owner=[{}]", targetContainer.getId(), targetContainer.getOwner().getId());
        
        researchContainerTask = taskService.openTask(this::researchContainer);
        log.info("Created research task, id=[{}]", researchContainerTask.getId());
    }

    private Result<Void> researchContainer(final Agent agent) {
        return Result.ok();
    }

    private Result<Void> moveToContainerByRoute(final Agent agent, final KnownObject targetContainer, final List<KnownObject> route) {
        route.remove(agent.getCharacter());
        route.remove(targetContainer);
        return MoveByRoute.perform(agent, route);
    }


}
