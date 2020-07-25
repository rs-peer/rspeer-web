package org.rspeer.pathfinder.graph.model.graph;

import org.rspeer.pathfinder.graph.model.rs.Position;

import java.util.Collection;
import java.util.Set;

public interface Graph<T extends Node> {

    Collection<T> getNodes();

    Set<T> buildPath(Position from, Position to);
}
