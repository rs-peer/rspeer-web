package org.rspeer.pathfinder.graph.algorithm.pathing.web.dijkstra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rspeer.pathfinder.graph.algorithm.pathing.local.dijkstra.DijkstraAlgorithm;
import org.rspeer.pathfinder.graph.algorithm.pathing.web.IGraphPathAlgorithm;
import org.rspeer.pathfinder.graph.model.hpa.HpaEdge;
import org.rspeer.pathfinder.graph.model.hpa.HpaGraph;
import org.rspeer.pathfinder.graph.model.hpa.HpaNode;
import org.rspeer.pathfinder.graph.model.rs.CollectiveFlagsProvider;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.RegionFlags;
import org.rspeer.pathfinder.graph.service.RegionFlagsService;
import org.rspeer.pathfinder.graph.util.MapFlags;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiPredicate;

@RequiredArgsConstructor
@Component
@Slf4j
public class WebDijkstraAlgorithm implements IGraphPathAlgorithm<HpaGraph, HpaNode> {

    private final DijkstraAlgorithm localDijkstra;
    private final RegionFlagsService regionFlagsService;

    @Override
    public Collection<HpaNode> buildPath(Position from, Position to, HpaGraph graph) {
        HpaNode region = graph.getInternalNode(from, 64);
        HpaNode endRegion = graph.getInternalNode(to, 64);
        if (region == null || endRegion == null) {
            return Collections.emptyList();
        }

        HpaNode atomicFrom = closest(region, from);
        HpaNode atomicTo = closest(endRegion, to);
        if (atomicFrom == null || atomicTo == null) {
            return Collections.emptyList();
        }

        log.info("Starting from {} and going to {}", atomicFrom.getRoot(), atomicTo.getRoot());

        Map<HpaNode, Double> scoreMap = new HashMap<>();
        scoreMap.put(atomicFrom, 0d);

        PriorityQueue<HpaNode> visiting = new PriorityQueue<>(Comparator.comparingDouble(scoreMap::get));
        visiting.add(atomicFrom);
        while (!visiting.isEmpty()) {
            HpaNode current = visiting.remove();
            double currCost = scoreMap.get(current);
            double currFinishCost = scoreMap.getOrDefault(atomicTo, Double.MAX_VALUE);
            if (current.equals(atomicTo) || currCost > currFinishCost) {
                continue;
            }

            for (HpaEdge edge : current.outgoing()) {
                log.info("Evaluating edge {} -> {}", edge.getStart().getRoot(), edge.getEnd().getRoot());
                HpaNode end = edge.getEnd();
                double cost = edge.getCost() + currCost;
                double existingCost = scoreMap.getOrDefault(end, Double.MAX_VALUE);
                if (cost < existingCost) {
                    scoreMap.put(end, cost);
                    visiting.add(end);
                }
            }
        }

        System.out.println(scoreMap.get(atomicTo));

        return null;
    }

    private HpaNode closest(HpaNode container, Position to) {
        final RegionFlags flags = regionFlagsService.getFor(container.getRoot());
        if (flags == null) {
            return null;
        }

        final CollectiveFlagsProvider cvp = new CollectiveFlagsProvider(new RegionFlags[]{flags});
        final BiPredicate<Position, Position> walkablePredicate = (p1, p2) -> MapFlags.isWalkableFrom(p1, cvp, true, true).test(p2);

        double dist = Double.MAX_VALUE;
        HpaNode closest = null;
        for (HpaNode child : container.getChildren()) {
            double currDist = localDijkstra.getDistanceEvaluator().distance(to, child.getRoot());
            if (currDist > dist) continue;

            boolean reachable = localDijkstra.isReachable(to, child.getRoot(), walkablePredicate);
            if (reachable) {
                dist = currDist;
                closest = child;
            }
        }

        return closest;
    }

}
