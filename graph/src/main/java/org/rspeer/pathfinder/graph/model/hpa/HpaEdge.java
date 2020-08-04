package org.rspeer.pathfinder.graph.model.hpa;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.rspeer.pathfinder.graph.model.graph.Edge;

@AllArgsConstructor
@EqualsAndHashCode
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
        return 1;
    }
}
