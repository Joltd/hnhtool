package com.evgenltd.hnhtool.harvester.research.service;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtool.harvester.common.service.Module;
import com.evgenltd.hnhtool.harvester.common.service.TaskService;
import com.evgenltd.hnhtool.harvester.research.command.MoveByRoute;
import com.evgenltd.hnhtool.harvester.research.command.OpenInventory;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.Inventory;
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

    private TaskService taskService;
    private RoutingService routingService;

    public WarehouseService(
            final TaskService taskService,
            final RoutingService routingService
    ) {
        this.taskService = taskService;
        this.routingService = routingService;
    }

    public void checkContainer(final KnownObject container) {
        taskService.openTask(agent -> checkContainerWork(agent, container));
    }

    // ##################################################
    // #                                                #
    // #                                                #
    // #                                                #
    // ##################################################

    private Result<Void> checkContainerWork(final Agent agent, final KnownObject targetContainer) {

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
