package com.evgenltd.hnhtool.harvester.research.service;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Work;
import com.evgenltd.hnhtool.harvester.common.repository.TaskRepository;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtool.harvester.common.service.Module;
import com.evgenltd.hnhtool.harvester.research.command.MoveByRoute;
import com.evgenltd.hnhtool.harvester.research.command.OpenInventory;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.Inventory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 08-04-2019 21:41</p>
 */
@Service
public class WarehouseService implements Module {

    private static final String CHECK_CONTAINER = "CHECK_CONTAINER";

    private RoutingService routingService;
    private TaskRepository taskRepository;

    private KnownObject targetContainer;

    public void checkContainer(final KnownObject container) {
        targetContainer = container;
        taskRepository.openTask(getClass(), CHECK_CONTAINER);
    }

    @Override
    @NotNull
    public Work getTaskWork(final String step) {
        if (step.equals(CHECK_CONTAINER)) {
            return this::checkContainerWork;
        }
        return agent -> Result.ok();
    }

    // ##################################################
    // #                                                #
    // #                                                #
    // #                                                #
    // ##################################################

    private Result<Void> checkContainerWork(final Agent agent) {

        final KnownObject character = agent.getCharacter();
        return routingService.route(character, targetContainer)
                .thenApply(route -> {
                    route.remove(character);
                    route.remove(targetContainer);
                    return MoveByRoute.perform(agent, route);
                })
                .thenApplyCombine(p -> OpenInventory.perform(agent, targetContainer))
                .thenApply(this::storeItemInfo);

    }

    private Void storeItemInfo(final Inventory inventory) {
        return null;
    }

}
