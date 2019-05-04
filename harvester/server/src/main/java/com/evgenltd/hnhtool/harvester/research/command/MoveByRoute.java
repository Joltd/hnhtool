package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Move;
import com.evgenltd.hnhtool.harvester.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
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

    private MoveByRoute(final List<KnownObject> route) {
        this.agent = TaskContext.getAgent();
        this.route = route;
    }

    @TaskRequired
    public static Result<Void> perform(@NotNull final List<KnownObject> route) {
        Assert.valueRequireNonEmpty(route, "Route");
        return new MoveByRoute(route).performImpl();
    }

    @TaskRequired
    public static Result<Void> performWithoutFromAndTo(@NotNull final List<KnownObject> route) {
        Assert.valueRequireNonEmpty(route, "Route");
        if (route.size() > 0) {
            route.remove(0);
        }
        if (route.size() > 0) {
            route.remove(route.size() - 1);
        }
        return new MoveByRoute(route).performImpl();
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

            return MoveToSpace.perform(doorway).anyway(() -> doorway = null);
        }

        doorway = null;

        return Move.perform(agent.getClient(), knownObject.getPosition());

    }

}
