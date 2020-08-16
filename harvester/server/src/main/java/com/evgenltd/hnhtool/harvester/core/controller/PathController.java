package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.service.RoutingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/path")
public class PathController {

    private final RoutingService routingService;

    public PathController(final RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping
    public Response<List<RoutingService.Edge>> list() {
        return new Response<>(routingService.list());
    }

    @PostMapping
    public Response<List<RoutingService.Edge>> update(@RequestBody final List<RoutingService.Edge> edges) {
        routingService.update(edges);
        return list();
    }

}
