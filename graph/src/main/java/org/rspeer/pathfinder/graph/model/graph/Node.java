package org.rspeer.pathfinder.graph.model.graph;

import java.util.Set;

public interface Node {

    Set<? extends Edge> outgoing();

    Set<? extends Edge> incoming();
}
