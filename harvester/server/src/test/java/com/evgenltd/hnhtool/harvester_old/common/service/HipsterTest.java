package com.evgenltd.hnhtool.harvester_old.common.service;

import com.evgenltd.hnhtools.entity.IntPoint;
import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.impl.WeightedNode;
import es.usc.citius.hipster.model.problem.SearchProblem;
import org.junit.Test;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 22:05</p>
 */
public class HipsterTest {

    @Test
    public void test() {

        final IntPoint character = new IntPoint(-923392, -922112);
        final IntPoint houseHatch = new IntPoint(-919168, -919040);
        final IntPoint cellarHatch = new IntPoint(109168, 109040);
        final IntPoint cellarCupboard = new IntPoint(103168, 103040);
        final IntPoint houseDoor = new IntPoint(-918528, -921600);

        final HipsterGraph<IntPoint, Double> graph = GraphBuilder.<IntPoint,Double>create()
                .connect(character).to(houseDoor).withEdge(calculateDistance(character, houseDoor))
                .connect(character).to(houseHatch).withEdge(calculateDistance(character, houseHatch))
                .connect(houseHatch).to(cellarHatch).withEdge(0D)
                .connect(cellarHatch).to(cellarCupboard).withEdge(calculateDistance(cellarCupboard, cellarCupboard))
                .createUndirectedGraph();

        final SearchProblem<Double, IntPoint, WeightedNode<Double, IntPoint, Double>> problem = GraphSearchProblem.startingFrom(
                character)
                .in(graph)
                .takeCostsFromEdges()
                .build();

        final Algorithm<Double, IntPoint, WeightedNode<Double, IntPoint, Double>>.SearchResult result = Hipster.createDijkstra(
                problem)
                .search(cellarCupboard);

        System.out.println(result);

    }

    private Double calculateDistance(final IntPoint from, final IntPoint to) {
        return Math.abs(Math.sqrt(Math.pow(to.getX() - from.getX(), 2)
                + Math.pow(to.getY() - from.getY(), 2)));
    }

}
