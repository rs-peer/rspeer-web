package org.rspeer.pathfinder.graph.model.hpa;

import lombok.AllArgsConstructor;
import org.rspeer.pathfinder.graph.model.graph.Edge;

@AllArgsConstructor
public class HpaEdge implements Edge {

    private final HpaNode start;
    private final HpaNode end;

    @Override
    public HpaNode getStart() {
        return start;
    }

    @Override
    public HpaNode getEnd() {
        return end;
    }

    @Override
    public double getCost() {
        return 0;
    }
}
