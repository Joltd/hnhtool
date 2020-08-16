package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.entity.WorldPoint;
import com.evgenltd.hnhtool.harvester.core.service.RoutingService;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.List;

public class Routing {

    private final Agent agent;

    public Routing(final Agent agent) {
        this.agent = agent;
    }

    public void move(final IntPoint position) {
        final Space space = agent.getCurrentSpace();
        final List<RoutingService.Node> route = agent.getRoutingService().route(
                WorldPoint.of(space, agent.getCharacterPosition()),
                WorldPoint.of(space, position)
        );

        for (final RoutingService.Node node : route) {
            agent.move(node.position());
        }
    }

}
