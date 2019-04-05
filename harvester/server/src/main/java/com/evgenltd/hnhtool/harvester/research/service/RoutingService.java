package com.evgenltd.hnhtool.harvester.research.service;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.research.entity.Path;
import com.evgenltd.hnhtool.harvester.research.entity.RoutingResultCode;
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
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    private PathRepository pathRepository;

    public RoutingService(final PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    public Result<List<KnownObject>> route(final KnownObject from, final KnownObject to) {

        final List<Path> network = pathRepository.findAll();
        if (network.isEmpty()) {
            return Result.fail(RoutingResultCode.NETWORK_NOT_EXISTS);
        }

        final Result<Path> fromPath = connectLocation(from, network).peek(network::add);
        if (fromPath.isFailed()) {
            return Result.fail(RoutingResultCode.FROM_POINT_UNREACHABLE);
        }

        final Result<Path> toPath = connectLocation(to, network).peek(network::add);
        if (toPath.isFailed()) {
            return Result.fail(RoutingResultCode.TO_POINT_UNREACHABLE);
        }

        final HipsterGraph<KnownObject, Double> graph = prepareGraph(network);

        final SearchProblem<Double, KnownObject, WeightedNode<Double, KnownObject, Double>> problem = GraphSearchProblem.startingFrom(
                from)
                .in(graph)
                .takeCostsFromEdges()
                .build();

        final Algorithm<Double, KnownObject, WeightedNode<Double, KnownObject, Double>>.SearchResult result = Hipster.createDijkstra(problem)
                .search(to);

        final List<List<KnownObject>> optimalPaths = result.getOptimalPaths();
        if (optimalPaths.isEmpty()) {
            return Result.fail(RoutingResultCode.ROUTE_NOT_FOUND);
        }

        return Result.ok(optimalPaths.get(0));

    }

    private Result<Path> connectLocation(final KnownObject targetObject, final List<Path> network) {
        final Space space = targetObject.getOwner();

        final Optional<Path> nearestPath = network.stream()
                .flatMap(path -> Stream.of(path.getFrom(), path.getTo()))
                .filter(knownObject -> knownObject.getOwner().getId().equals(space.getId()))
                .map(knownObject -> dummyPath(knownObject, targetObject))
                .min(Comparator.comparingDouble(Path::getDistance));
        if (!nearestPath.isPresent()) {
            return Result.fail(RoutingResultCode.TARGET_UNREACHABLE);
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
