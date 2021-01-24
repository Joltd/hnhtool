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
    public List<RoutingService.Edge> list() {
        return routingService.list();
    }

    @PostMapping
    public List<RoutingService.Edge> update(@RequestBody final List<RoutingService.Edge> edges) {
        routingService.update(edges);
        return list();
    }

}
