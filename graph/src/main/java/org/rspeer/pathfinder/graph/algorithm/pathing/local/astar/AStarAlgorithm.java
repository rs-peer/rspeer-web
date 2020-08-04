package org.rspeer.pathfinder.graph.algorithm.pathing.local.astar;

import org.rspeer.pathfinder.graph.algorithm.pathing.local.ILocalPathAlgorithm;
import org.rspeer.pathfinder.graph.model.rs.Position;

import java.util.function.BiPredicate;

public class AStarAlgorithm implements ILocalPathAlgorithm {

    @Override
    public boolean isReachable(Position from, Position to, BiPredicate<Position, Position> excluding) {
        return false;
    }
}
