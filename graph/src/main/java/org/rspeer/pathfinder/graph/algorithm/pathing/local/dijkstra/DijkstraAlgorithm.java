package org.rspeer.pathfinder.graph.algorithm.pathing.local.dijkstra;

import lombok.RequiredArgsConstructor;
import org.rspeer.pathfinder.graph.algorithm.distance.IDistanceEvaluator;
import org.rspeer.pathfinder.graph.algorithm.pathing.local.ILocalPathAlgorithm;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.BiPredicate;

@Component
@RequiredArgsConstructor
public class DijkstraAlgorithm implements ILocalPathAlgorithm {

    private final IDistanceEvaluator distanceEvaluator;

    public IDistanceEvaluator getDistanceEvaluator() {
        return distanceEvaluator;
    }

    @Override
    public boolean isReachable(Position from, Position to, BiPredicate<Position, Position> excluding) {
        return isReachable(from, to, excluding, Integer.MAX_VALUE);
    }

    public boolean isReachable(Position from, Position to, BiPredicate<Position, Position> excluding, int maxDistance) {
        Map<Position, Integer> score = new HashMap<>();
        score.put(from, 0);

        PriorityQueue<Position> visiting = new PriorityQueue<>(Comparator.comparingDouble(p -> score.getOrDefault(p, Integer.MAX_VALUE)));
        visiting.add(from);

        while (!visiting.isEmpty()) {
            Position current = visiting.remove();
            if (current.equals(to) && score.getOrDefault(to, Integer.MAX_VALUE) <= maxDistance) {
                return true;
            }

            int currentScore = score.get(current);
            if (currentScore > maxDistance) {
                continue;
            }

            for (Position neighbour : current.getNeighbouringPositions(nb -> excluding.test(current, nb))) {
                int newScore = currentScore + 1;
                int neighbourScore = score.getOrDefault(neighbour, Integer.MAX_VALUE);
                if (newScore < neighbourScore) {
                    score.put(neighbour, newScore);
                    visiting.add(neighbour);
                }
            }
        }

        return false;
    }
}
