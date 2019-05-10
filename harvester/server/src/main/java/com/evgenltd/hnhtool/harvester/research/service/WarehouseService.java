package com.evgenltd.hnhtool.harvester.research.service;

import com.evgenltd.hnhtool.harvester.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester.common.entity.KnownItem;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.repository.KnownItemRepository;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtool.harvester.research.command.*;
import com.evgenltd.hnhtool.harvester.research.entity.ResearchResultCode;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.entity.WorldStack;
import com.evgenltd.hnhtools.entity.ResultCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 01-05-2019 13:58</p>
 */
@Service
public class WarehouseService {

    private static final Logger log = LogManager.getLogger(WarehouseService.class);

    // may add sorting by distance?
    private static final Comparator<KnownObject> CONTAINER_PRIORITY_COMPARATOR = (o1, o2) -> {
        if (o1.getStack() == o2.getStack()) {
            return o1.getResource().compareTo(o2.getResource());
        }
        return o1.getStack() && !o2.getStack()
                ? -1
                : 1;
    };

    private KnownObjectRepository knownObjectRepository;
    private KnownItemRepository knownItemRepository;
    private RoutingService routingService;

    public WarehouseService(
            final KnownObjectRepository knownObjectRepository,
            final KnownItemRepository knownItemRepository,
            final RoutingService routingService
    ) {
        this.knownObjectRepository = knownObjectRepository;
        this.knownItemRepository = knownItemRepository;
        this.routingService = routingService;
    }

    /**
     * <p>Takes item from warehouse and placing it in character inventory</p>
     */
    @TaskRequired
    public Result<KnownItem> takeItem(@NotNull final KnownItem knownItem) {

        if (knownItem.getOwner() == null) {
            return Result.fail(ResearchResultCode.ITEM_BELONGS_TO_ANOTHER_ITEM);
        }

        final KnownObject container = knownObjectRepository.findById(knownItem.getOwner().getId()).orElse(null);
        if (container == null) {
            return Result.fail(ResearchResultCode.KNOWN_OBJECT_NOT_FOUND);
        }

        return routingService.route(TaskContext.getAgent().getCharacter(), container)
                .thenApplyCombine(MoveByRoute::performWithoutFromAndTo)
                .thenCombine(() -> OpenContainer.perform(container))
                .then(() -> TaskContext.getAgent().matchItemKnowledge())
                .thenCombine(() -> container.getStack()
                        ? takeItemFromStack(knownItem)
                        : takeItemFromContainer(knownItem)
                );

    }

    /**
     * <p>Store item in warehouse, assume that before operation item stored in character inventory</p>
     */
    @TaskRequired
    public Result<KnownItem> storeItem(@NotNull final KnownItem knownItem) {
        final Agent agent = TaskContext.getAgent();
        final KnownObject character = agent.getCharacter();
        final Long characterId = character.getId();
        if (knownItem.getOwner() == null || !knownItem.getOwner().getId().equals(characterId)) {
            return Result.fail(ResearchResultCode.NOT_IN_MAIN_INVENTORY);
        }

        final List<KnownObject> containerCandidates = selectSuitableContainers(knownItem);
        for (final KnownObject containerCandidate : containerCandidates) {

            final Result<Void> result = routingService.route(character, containerCandidate)
                    .thenApplyCombine(MoveByRoute::performWithoutFromAndTo)
                    .thenCombine(() -> OpenContainer.perform(containerCandidate))
                    .then(agent::matchItemKnowledge);
            if (result.isFailed()) {
                log.warn("Unable to access to a container [{}], result [{}]", containerCandidate.getId(), result.getCode());
                continue;
            }

            final Result<KnownItem> storingResult = containerCandidate.getStack()
                    ? storeItemInStack(containerCandidate, knownItem)
                    : storeItemInContainer(containerCandidate, knownItem);
            if (storingResult.isFailed()) {
                log.warn("Unable to store item in container [{}], result = [{}]", containerCandidate.getId(), storingResult.getCode());
                continue;
            }

            return storingResult;

        }

        return Result.fail(ResearchResultCode.NO_SUITABLE_CONTAINER_FOUND);
    }

    public Result<Void> digIntoStack(final KnownObject stack, final int depth) {
        final Result<WorldStack> worldStack = TaskContext.getAgent().getMatchedWorldObjectId(stack.getId())
                .thenApplyCombine(woId -> TaskContext.getClient().getStack(woId));
        if (worldStack.isFailed()) {
            return worldStack.cast();
        }

        final Integer currentCount = worldStack.getValue().getCount();

        for (int count = currentCount; count > depth; count--) {
            final Result<Void> takingResult = TakeItemFromStack.perform(stack)
                    .thenCombine(DropItemInWorld::perform);
            if (takingResult.isFailed()) {
                return takingResult.cast();
            }
        }

        knownItemRepository.deleteFromStack(stack.getId(), depth);

        final KnownObject actualStack = knownObjectRepository.findById(stack.getId()).orElse(null);
        if (actualStack == null) {
            return Result.fail(ResultCode.NO_STACK);
        }
        actualStack.setCount(depth);
        knownObjectRepository.save(actualStack);

        return Result.ok();
    }

    // ##################################################
    // #                                                #
    // #  Taking item                                   #
    // #                                                #
    // ##################################################

    private Result<KnownItem> takeItemFromStack(final KnownItem knownItem) {
        final KnownObject character = TaskContext.getAgent().getCharacter();
        return ResourceConstants.getSize(knownItem.getResource())
                .thenApplyCombine(itemSize -> InventorySolver.getFreeSlot(character, itemSize))
                .thenApplyCombine(freeSlot -> digIntoStack(knownItem.getOwner(), knownItem.getX())
                        .thenCombine(() -> TakeItemFromStack.perform(knownItem.getOwner()))
                        .thenCombine(() -> DropItemInInventory.perform(character.getId(), freeSlot))
                        .thenCombine(() -> knownItemRepository.findByPosition(character.getId(), freeSlot))
                );
    }

    private Result<KnownItem> takeItemFromContainer(final KnownItem knownItem) {
        final KnownObject character = TaskContext.getAgent().getCharacter();
        return ResourceConstants.getSize(knownItem.getResource())
                .thenApplyCombine(itemSize -> InventorySolver.getFreeSlot(character, itemSize))
                .thenApplyCombine(freeSlot -> TakeItem.perform(knownItem)
                        .thenCombine(() -> DropItemInInventory.perform(character.getId(), freeSlot))
                        .thenCombine(() -> knownItemRepository.findByPosition(character.getId(), freeSlot))
                );
    }

    // ##################################################
    // #                                                #
    // #  Storing item                                  #
    // #                                                #
    // ##################################################

    private List<KnownObject> selectSuitableContainers(final KnownItem knownItem) {
        final String matchedStackResource = ResourceConstants.getMatchedResource(knownItem.getResource());
        return knownObjectRepository.findSuitableContainers(matchedStackResource)
                .stream()
                .sorted(CONTAINER_PRIORITY_COMPARATOR)
                .collect(Collectors.toList());
    }

    private Result<KnownItem> storeItemInContainer(final KnownObject container, final KnownItem item) {
        return ResourceConstants.getSize(item.getResource())
                .thenApplyCombine(itemSize -> InventorySolver.getFreeSlot(container, itemSize))
                .thenApplyCombine(freeSlot -> TakeItem.perform(item)
                        .thenCombine(() -> DropItemInInventory.perform(container.getId(), freeSlot))
                        .thenCombine(() -> knownItemRepository.findByPosition(container.getId(), freeSlot))
                );
    }

    private Result<KnownItem> storeItemInStack(final KnownObject stack, final KnownItem item) {
        if (!stack.getResearched()) {
            return Result.fail(ResearchResultCode.STACK_NOT_RESEARCHED);
        }

        return TransferItem.perform(item)
                .then(() -> {
                    stack.setCount(stack.getCount() + 1);
                    knownObjectRepository.save(stack);
                    item.setId(null);
                    item.setOwner(stack);
                    item.setX(stack.getCount());
                    item.setY(0);
                    return knownItemRepository.save(item);
                });
    }

}
