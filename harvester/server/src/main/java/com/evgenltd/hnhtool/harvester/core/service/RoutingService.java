package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.WorldPoint;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.impl.WeightedNode;
import es.usc.citius.hipster.model.problem.SearchProblem;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RoutingService {

    private static final String GRAPH_FILENAME = "path.json";

    private HipsterGraph<Node, Double> graph;

    private final ObjectMapper objectMapper;
    private final KnownObjectRepository knownObjectRepository;

    public RoutingService(
            final ObjectMapper objectMapper,
            final KnownObjectRepository knownObjectRepository
    ) {
        this.objectMapper = objectMapper;
        this.knownObjectRepository = knownObjectRepository;
    }

    @PostConstruct
    public void postConstruct() {
        load();
    }

    public List<Edge> list() {
        return StreamSupport.stream(graph.edges().spliterator(), false)
                .map(graphEdge -> new Edge(graphEdge.getVertex1(), graphEdge.getVertex2()))
                .collect(Collectors.toList());
    }

    public void update(final List<Edge> edges) {
        buildGraph(edges);
        save();
    }

    public List<Node> route(final WorldPoint from, final WorldPoint to) {

        final Node nearestFrom = findNearest(from);
        final Node nearestTo = findNearest(to);

        final SearchProblem<Double, Node, WeightedNode<Double, Node, Double>> problem = GraphSearchProblem.startingFrom(nearestFrom)
                .in(graph)
                .takeCostsFromEdges()
                .build();

        final Algorithm<Double, Node, WeightedNode<Double, Node, Double>>.SearchResult result = Hipster.createDijkstra(problem).search(nearestTo);

        final List<List<Node>> optimalPaths = result.getOptimalPaths();
        if (optimalPaths.isEmpty()) {
            throw new ApplicationException("Optimal route not found");
        }

        final List<Node> route = optimalPaths.get(0);
        Node prev = null;
        for (Iterator<Node> iterator = route.iterator(); iterator.hasNext(); ) {
            final Node node = iterator.next();
            if (prev != null && prev.isDoorway() && node.isDoorway()) {
                iterator.remove();
            } else {
                prev = node;
            }
        }

        return route;

    }

    // ##################################################
    // #                                                #
    // #  Save/Load                                     #
    // #                                                #
    // ##################################################

    private void load() {
        final File file = new File(GRAPH_FILENAME);
        if (!file.exists()) {
            graph = GraphBuilder.<Node, Double>create().createUndirectedGraph();
            save();
            return;
        }

        try {
            final List<Edge> edges = objectMapper.readValue(file, new TypeReference<>() {});
            buildGraph(edges);
        } catch (final IOException e) {
            throw new ApplicationException(e);
        }
    }

    private void save() {
        final List<Edge> toSave = StreamSupport.stream(graph.edges().spliterator(), false)
                .map(graphEdge -> new Edge(graphEdge.getVertex1(), graphEdge.getVertex2()))
                .collect(Collectors.toList());

        final File file = new File(GRAPH_FILENAME);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, toSave);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    // ##################################################
    // #                                                #
    // #  Utils                                         #
    // #                                                #
    // ##################################################

    private void buildGraph(final List<Edge> edges) {
        final GraphBuilder<Node, Double> graphBuilder = GraphBuilder.create();
        for (final Edge edge : edges) {
            final Node from = loadDoorway(edge.from());
            final Node to = loadDoorway(edge.to());

            graphBuilder.connect(from)
                    .to(to)
                    .withEdge(calculateDistance(from, to));
        }
        graph = graphBuilder.createUndirectedGraph();
    }

    private Node loadDoorway(final Node node) {
        if (!node.isDoorway()) {
            return node;
        }

        final KnownObject knownObject = knownObjectRepository.loadById(node.knownObjectId());
        return new Node(node.knownObjectId(), knownObject.getSpace().getId(), knownObject.getPosition());
    }

    private Double calculateDistance(final Node from, final Node to) {
        if (from.isDoorway() && to.isDoorway() && Objects.equals(from.spaceId(), to.spaceId())) {
            throw new ApplicationException("Spaces linkage should reference to different spaces");
        }

        if ((from.isDoorway() || to.isDoorway()) && !Objects.equals(from.spaceId(), to.spaceId())) {
            throw new ApplicationException("Doorway and point have different spaces");
        }

        return calculateDistance(from.position(), to.position());
    }

    private Double calculateDistance(final IntPoint from, final IntPoint to) {
        return Math.abs(Math.sqrt(
                Math.pow(to.getX() - from.getX(), 2) +
                        Math.pow(to.getY() - from.getY(), 2)
        ));
    }

    private Node findNearest(final WorldPoint target) {
        return StreamSupport.stream(graph.vertices().spliterator(), false)
                .filter(node -> Objects.equals(node.spaceId(), target.getSpace().getId()))
                .map(point -> new DistanceTo(point, calculateDistance(point.position(), target.getPosition())))
                .min(Comparator.comparing(DistanceTo::distance))
                .map(DistanceTo::node)
                .orElseThrow(() -> new ApplicationException("No linkage with path graph"));
    }

    // ##################################################
    // #                                                #
    // #  Records                                       #
    // #                                                #
    // ##################################################

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static final record Node(Long knownObjectId, Long spaceId, IntPoint position) {
        boolean isDoorway() {
            return knownObjectId != null;
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static final record Edge(Node from, Node to) {}

    private static final record DistanceTo(Node node, Double distance) {}

}
