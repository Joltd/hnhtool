package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.command.Move;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-04-2019 18:36</p>
 */
public class MoveByRoute {

    private Agent agent;
    private List<KnownObject> route;

    private KnownObject doorway = null;

    private MoveByRoute(final Agent agent, final List<KnownObject> route) {
        this.agent = agent;
        this.route = route;
    }

    public static Result<Void> perform(@NotNull final Agent agent, @NotNull final List<KnownObject> route) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(route, "Route");
        return new MoveByRoute(agent, route).performImpl();
    }

    private Result<Void> performImpl() {

        for (final KnownObject knownObject : route) {

            final Result<Void> result = moveTo(knownObject);
            if (result.isFailed()) {
                return result;
            }

        }

        return Result.ok();

    }

    private Result<Void> moveTo(final KnownObject knownObject) {
        if (knownObject.getDoorway()) {

            if (doorway == null) {
                doorway = knownObject;
                return Result.ok();
            }

            return MoveToSpace.perform(agent, doorway)
                    .anyway(() -> doorway = null);
        }

        doorway = null;

        return Move.perform(agent.getClient(), knownObject.getPosition());

    }

}
