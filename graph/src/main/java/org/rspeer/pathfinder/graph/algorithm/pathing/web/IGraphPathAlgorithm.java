package org.rspeer.pathfinder.graph.algorithm.pathing.web;

import org.rspeer.pathfinder.graph.model.graph.Graph;
import org.rspeer.pathfinder.graph.model.graph.Node;
import org.rspeer.pathfinder.graph.model.rs.Position;

import java.util.Collection;

public interface IGraphPathAlgorithm<T extends Graph, V extends Node> {

    Collection<V> buildPath(Position from, Position to, T graph);

}
