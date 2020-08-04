package org.rspeer.pathfinder.graph.algorithm.distance;

import org.rspeer.pathfinder.graph.model.rs.Position;

public interface IDistanceEvaluator {

    double distance(Position from, Position to);

}
