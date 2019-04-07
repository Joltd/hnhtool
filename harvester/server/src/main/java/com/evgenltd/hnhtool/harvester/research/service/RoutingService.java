package com.evgenltd.hnhtool.harvester.research.service;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.research.entity.Path;
import com.evgenltd.hnhtool.harvester.research.entity.ResearchResultCode;
import com.evgenltd.hnhtool.harvester.research.repository.PathRepository;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.impl.WeightedNode;
import es.usc.citius.hipster.model.problem.SearchProblem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 05-04-2019 01:04</p>
 */
@Service
public class RoutingService {

    private static final Logger log = LogManager.getLogger(RoutingService.class);

    private PathRepository pathRepository;

    public RoutingService(final PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    public Result<List<KnownObject>> route(final KnownObject from, final KnownObject to) {

        final List<Path> network = pathRepository.findAll();
        if (network.isEmpty()) {

            if (Objects.equals(from.getOwner().getId(), to.getOwner().getId())) {
                final List<KnownObject> directRoute = new ArrayList<>();
                directRoute.add(from);
                directRoute.add(to);
                return Result.ok(directRoute);
            }

            return Result.fail(ResearchResultCode.NETWORK_NOT_EXISTS);

        }

        final Result<Path> fromPath = connectLocation(from, network).thenAnyway(network::add);
        if (fromPath.isFailed()) {
            return Result.fail(ResearchResultCode.FROM_POINT_UNREACHABLE);
        }

        final Result<Path> toPath = connectLocation(to, network).thenAnyway(network::add);
        if (toPath.isFailed()) {
            return Result.fail(ResearchResultCode.TO_POINT_UNREACHABLE);
        }

        final HipsterGraph<KnownObject, Double> graph = prepareGraph(network);

        final SearchProblem<Double, KnownObject, WeightedNode<Double, KnownObject, Double>> problem = GraphSearchProblem.startingFrom(
                from)
                .in(graph)
                .takeCostsFromEdges()
                .build();

        final Algorithm<Double, KnownObject, WeightedNode<Double, KnownObject, Double>>.SearchResult result = Hipster.createDijkstra(problem)
                .search(to);

        log.info(String.format("Route found, %s", result));

        final List<List<KnownObject>> optimalPaths = result.getOptimalPaths();
        if (optimalPaths.isEmpty()) {
            return Result.fail(ResearchResultCode.ROUTE_NOT_FOUND);
        }

        return Result.ok(optimalPaths.get(0));

    }

    // add check if targetObject is already in network
    private Result<Path> connectLocation(final KnownObject targetObject, final List<Path> network) {
        final Space space = targetObject.getOwner();

        final Optional<Path> nearestPath = network.stream()
                .flatMap(path -> Stream.of(path.getFrom(), path.getTo()))
                .filter(knownObject -> knownObject.getOwner().getId().equals(space.getId()))
                .map(knownObject -> dummyPath(knownObject, targetObject))
                .min(Comparator.comparingDouble(Path::getDistance));
        if (!nearestPath.isPresent()) {
            return Result.fail(ResearchResultCode.TARGET_UNREACHABLE);
        }

        return Result.ok(nearestPath.get());
    }

    private Path dummyPath(final KnownObject fromKnownObject, final KnownObject toKnownObject) {
        final Path dummy = new Path();
        dummy.setFrom(fromKnownObject);
        dummy.setTo(toKnownObject);
        final Double distance = calculateDistance(fromKnownObject.getPosition(), toKnownObject.getPosition());
        dummy.setDistance(distance);
        return dummy;
    }

    private Double calculateDistance(final IntPoint from, final IntPoint to) {
        return Math.abs(Math.sqrt(
                Math.pow(to.getX() - from.getX(), 2)
                + Math.pow(to.getY() - from.getY(), 2)
        ));
    }

    private HipsterGraph<KnownObject, Double> prepareGraph(final List<Path> network) {
        final GraphBuilder<KnownObject, Double> builder = GraphBuilder.create();
        for (final Path path : network) {
            builder.connect(path.getFrom())
                    .to(path.getTo())
                    .withEdge(path.getDistance());
        }
        return builder.createUndirectedGraph();
    }

}
