package org.rspeer.pathfinder.graph.model.graph;

import java.util.Set;

public interface Node {

    Set<Edge> outgoing();

    Set<Edge> incoming();
}
