package org.rspeer.pathfinder.graph.algorithm.pathing.local;

import org.rspeer.pathfinder.graph.model.rs.Position;

import java.util.function.BiPredicate;

public interface ILocalPathAlgorithm {

    boolean isReachable(Position from, Position to, BiPredicate<Position, Position> excluding);

}
