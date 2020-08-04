package org.rspeer.pathfinder.graph.algorithm.distance.euclid;

import org.rspeer.pathfinder.graph.algorithm.distance.IDistanceEvaluator;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.springframework.stereotype.Component;

@Component
public class EuclidianEvaluator implements IDistanceEvaluator {

    @Override
    public double distance(Position from, Position to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2)
                + Math.pow(from.getLevel() - to.getLevel(), 2));
    }

}
