package com.evgenltd.hnhtool.harvester.research.service;

import com.evgenltd.hnhtool.harvester.common.command.CommandUtils;
import com.evgenltd.hnhtool.harvester.common.entity.KnownItem;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtool.harvester.common.service.Module;
import com.evgenltd.hnhtool.harvester.common.service.TaskService;
import com.evgenltd.hnhtool.harvester.research.command.MoveByRoute;
import com.evgenltd.hnhtools.common.Result;
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

    public void takeItemFromWarehouse(final Agent agent, final KnownItem knownItem) {
//        final KnownObject owner = knownItem.getOwner();
//        taskService.openTask(agent -> routingService.route(agent.getCharacter(), owner)
//                .thenApplyCombine(route -> MoveByRoute.performWithoutFromAndTo(agent, route))
//                .thenCombine(() -> OpenContainer.perform(agent, owner))
//                .thenCombine(() -> CommandUtils.await(() -> true))
//                .thenCombine(() -> TransferItem.perform(agent, knownItem)));
    }

    private void takeItemFromContainer(final KnownItem knownItem) {
        final KnownObject container = knownItem.getOwner();
        taskService.openTask(agent -> routingService.route(agent.getCharacter(), container)
                .thenApplyCombine(route -> MoveByRoute.performWithoutFromAndTo(agent, route))
                .thenCombine(() -> transferItem(agent, knownItem)));
    }

    public void putItemToWarehoue() {

    }

    private Result<Void> transferItem(final Agent agent, final KnownItem knownItem) {
        return agent.getMatchedWorldItemId(knownItem.getId())
                .then(woId -> agent.getClient().transferItem(woId))
                .then(woId -> CommandUtils.await(() -> agent.getMatchedKnownItemId(woId).isFailed()))
                .cast();

    }

}
