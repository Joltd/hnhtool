package com.evgenltd.hnhtool.harvester_old.research.service;

import com.evgenltd.hnhtool.harvester.core.entity.KnownItem;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownItemRepository;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester_old.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester_old.common.entity.Task;
import com.evgenltd.hnhtool.harvester_old.common.service.Agent;
import com.evgenltd.hnhtool.harvester_old.common.service.Module;
import com.evgenltd.hnhtool.harvester_old.common.service.TaskService;
import com.evgenltd.hnhtool.harvester_old.research.command.MoveByRoute;
import com.evgenltd.hnhtool.harvester_old.research.command.MoveToSpace;
import com.evgenltd.hnhtool.harvester_old.research.command.OpenContainer;
import com.evgenltd.hnhtool.harvester_old.research.command.TakeItemFromWorld;
import com.evgenltd.hnhtool.harvester_old.research.entity.Path;
import com.evgenltd.hnhtool.harvester_old.research.entity.ResearchResultCode;
import com.evgenltd.hnhtool.harvester_old.research.repository.PathRepository;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.entity.WorldStack;
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
    private KnownItemRepository knownItemRepository;
    private TaskService taskService;
    private PathRepository pathRepository;
    private RoutingService routingService;
    private WarehouseService warehouseService;

    private Task researchDoorwayTask;
    private Task researchContainerTask;
    private Task searchForDroppedItemsTask;

    public ResearchService(
            final KnownObjectRepository knownObjectRepository,
            final KnownItemRepository knownItemRepository,
            final TaskService taskService,
            final PathRepository pathRepository,
            final RoutingService routingService,
            final WarehouseService warehouseService
    ) {
        this.knownObjectRepository = knownObjectRepository;
        this.knownItemRepository = knownItemRepository;
        this.taskService = taskService;
        this.pathRepository = pathRepository;
        this.routingService = routingService;
        this.warehouseService = warehouseService;
    }

    @Scheduled(fixedDelay = 10_000L)
    public void main() {
//        scheduleDoorwayResearch();
        scheduleContainerResearch();
        scheduleSearchForDroppedItems();
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

        researchDoorwayTask = taskService.openTask(() -> researchDoorway(targetDoorway));
        log.info("Created research task, id=[{}]", researchDoorwayTask.getId());
    }

    private Result<Void> researchDoorway(final KnownObject targetDoorway) {

        return routingService.route(TaskContext.getAgent().getCharacter(), targetDoorway)
                .thenApplyCombine(route -> moveToDoorwayByRoute(route, targetDoorway))
                .thenCombine(() -> MoveToSpace.perform(targetDoorway))
                .thenApplyCombine(characterPosition -> storeDoorwayResearchResult(targetDoorway));
    }

    private Result<Void> moveToDoorwayByRoute(
            final List<KnownObject> route,
            final KnownObject targetDoorway
    ) {

        route.remove(TaskContext.getAgent().getCharacter());
        route.remove(targetDoorway);
        return MoveByRoute.perform(route);
    }

    private Result<Void> storeDoorwayResearchResult(final KnownObject targetDoorway) {
        final Agent agent = TaskContext.getAgent();
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
        
        researchContainerTask = taskService.openTask(() -> researchContainer(targetContainer));
        log.info("Created research task, id=[{}]", researchContainerTask.getId());
    }

    private Result<Void> researchContainer(final KnownObject targetContainer) {
        return routingService.route(TaskContext.getAgent().getCharacter(), targetContainer)
                .thenApplyCombine(MoveByRoute::performWithoutFromAndTo)
                .thenCombine(() -> OpenContainer.perform(targetContainer))
                .then(() -> TaskContext.getAgent().matchItemKnowledge())
                .thenCombine(() -> markContainerAsResearched(targetContainer));
    }

    private Result<Void> markContainerAsResearched(final KnownObject container) {
        if (container.getStack()) {
            final Result<WorldStack> result = TaskContext.getAgent().getMatchedWorldObjectId(container.getId())
                    .thenApplyCombine(woId -> TaskContext.getClient().getStack(woId))
                    .then(stack -> {
                        container.setCount(stack.getCount());
                        container.setMax(stack.getMax());
                        knownObjectRepository.markAsResearched(container);
                    });
            if (result.isFailed()) {
                return result.cast();
            }
            return warehouseService.digIntoStack(container, 1);
        }

        return InventorySolver.fillItemCount(container)
                .then(() -> knownObjectRepository.markAsResearched(container));
    }

    // ##################################################
    // #                                                #
    // #  Search dropped items                          #
    // #                                                #
    // ##################################################

    private void scheduleSearchForDroppedItems() {
        if (searchForDroppedItemsTask != null && !searchForDroppedItemsTask.getStatus().isFinished()) {
            return;
        }

        final List<KnownObject> droppedItems = knownObjectRepository.findDroppedItems();
        if (droppedItems.isEmpty()) {
            return;
        }

        searchForDroppedItemsTask = taskService.openTask(() -> collectDroppedItems(droppedItems));
    }

    private Result<Void> collectDroppedItems(final List<KnownObject> droppedItems) {
        final KnownObject character = TaskContext.getAgent().getCharacter();

        boolean storeItemsToWarehouse = false;

        for (final KnownObject droppedItem : droppedItems) {

            if (TaskContext.getAgent().getMatchedWorldObjectId(droppedItem.getId()).isFailed()) {
                knownObjectRepository.delete(droppedItem);
                continue;
            }

            final Result<String> matchedItem = ResourceConstants.getMatch(droppedItem.getResource());
            if (matchedItem.isFailed()) {
                log.warn("Dropped Item [{}] fail to collect {}", droppedItem.getId(), matchedItem);
                continue;
            }

            final Result<IntPoint> itemSize = ResourceConstants.getSize(matchedItem.getValue());
            if (itemSize.isFailed()) {
                log.warn("Dropped Item [{}] fail to collect {}", droppedItem.getId(), itemSize);
                continue;
            }

            final Result<IntPoint> freeSlot = InventorySolver.getFreeSlot(
                    character,
                    itemSize.getValue()
            );
            if (freeSlot.isFailed()) {
                if (freeSlot.getCode().equals(ResearchResultCode.NOT_ENOUGH_SPACE_IN_INVENTORY)) {
                    break; // inventory is full
                } else {
                    log.warn("Dropped Item [{}] fail to collect {}", droppedItem.getId(), freeSlot);
                    continue;
                }
            }

            final Result<Void> takeResult = TakeItemFromWorld.perform(droppedItem);
            if (takeResult.isFailed()) {
                log.warn("Dropped Item [{}] fail to collect {}", droppedItem.getId(), takeResult);
                continue;
            }

            storeItemsToWarehouse = true;
        }

        if (!storeItemsToWarehouse) {
            return Result.ok();
        }

        TaskContext.getAgent().matchItemKnowledge();

        final List<KnownItem> characterItems = knownItemRepository.findByOwnerId(character.getId());
        for (final KnownItem characterItem : characterItems) {
            final Result<KnownItem> result = warehouseService.storeItem(characterItem);
            if (result.isFailed()) {
                log.warn("Collected Item [{}] fail to store {}", characterItem.getId(), result);
            }
        }

        return Result.ok();
    }

}
