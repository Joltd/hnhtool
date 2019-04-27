package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtool.harvester.research.entity.ResearchResultCode;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 27-04-2019 00:27</p>
 */
public class DropItemInInventory {

    private Agent agent;
    private Number inventoryOwnerId;
    private IntPoint position;

    private DropItemInInventory(final Agent agent, final Number inventoryOwnerId, final IntPoint position) {
        this.agent = agent;
        this.inventoryOwnerId = inventoryOwnerId;
        this.position = position;
    }

    public static Result<Void> performImpl(@NotNull final Agent agent, @NotNull final Number inventoryOwnerId, @NotNull final IntPoint position) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(inventoryOwnerId, "InventoryOwnerId");
        Assert.valueRequireNonEmpty(position, "Position");
        return new DropItemInInventory(agent, inventoryOwnerId, position).perform();
    }

    private Result<Void> perform() {
        if (agent.getClient().getHand() == null) {
            return Result.fail(ResearchResultCode.HAND_IS_EMPTY);
        }

        return agent.getClient().getInventoryByParentId(inventoryOwnerId)
                .then(inventory -> agent.getClient().dropItemFromHandInInventory(inventory.getId(), position))
                .then(() -> Await.performSimple(() -> agent.getClient().getHand() == null))
                .cast();
    }

}
