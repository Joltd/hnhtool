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
import com.evgenltd.hnhtools.entity.IntPoint;
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
                ? 1
                : -1;
    };

    private KnownObjectRepository knownObjectRepository;
    private KnownItemRepository knownItemRepository;
    private RoutingService routingService;

    /**
     * <p>Takes item from warehouse and placing it in character inventory</p>
     */
    @TaskRequired
    public Result<Void> takeItem(@NotNull final KnownItem knownItem) {
        // check main inventory have enough place
        // route to container
        // do one of the next:
        //   - if stack - dig into it
        //   - if regular inv - open it and take item
        // item placed in any empty slot(s)
        return Result.ok();
    }

    /**
     * <p>Store item in warehouse, assume that before operation item stored in character inventory</p>
     */
    public Result<Void> storeItem(@NotNull final KnownItem knownItem) {
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

            final KnownObject actualContainer = knownObjectRepository.findById(containerCandidate.getId()).orElse(null);
            if (actualContainer == null) {
                log.warn("Container candidate [{}] actually not found", containerCandidate.getId());
                continue;
            }

            final Result<Void> storingResult = actualContainer.getStack()
                    ? storeItemInStack(actualContainer, knownItem)
                    : storeItemInContainer(actualContainer, knownItem);
            if (storingResult.isFailed()) {
                log.warn("Unable to store item in container [{}], result = [{}]", actualContainer.getId(), storingResult.getCode());
                continue;
            }

            return storingResult;

        }

        return Result.fail(ResearchResultCode.NO_SUITABLE_CONTAINER_FOUND);
    }

    //


    private List<KnownObject> selectSuitableContainers(final KnownItem knownItem) {
        final String matchedStackResource = ResourceConstants.getMatchedStack(knownItem.getResource());
        return knownObjectRepository.findSuitableContainers(matchedStackResource)
                .stream()
                .sorted(CONTAINER_PRIORITY_COMPARATOR)
                .collect(Collectors.toList());
    }

    private Result<Void> storeItemInContainer(final KnownObject container, final KnownItem item) {
        // use inv resolver to determine position for storing

        return TakeItem.perform(item)
                .thenCombine(() -> DropItemInInventory.perform(container.getId(), new IntPoint()));
    }

    private Result<Void> storeItemInStack(final KnownObject stack, final KnownItem item) {
        if (!stack.getResearched()) {
            return Result.fail(ResearchResultCode.STACK_NOT_RESEARCHED);
        }

        return TransferItem.perform(item)
                .then(() -> {
                    item.setId(null);
                    item.setOwner(stack);
                    knownItemRepository.save(item);
                });
    }

}
