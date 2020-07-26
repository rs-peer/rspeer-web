package org.rspeer.pathfinder.graph.service.builder;

import org.rspeer.pathfinder.graph.model.graph.Graph;
import org.rspeer.pathfinder.graph.model.graph.Node;

public interface IGraphBuilderService<T extends Node> {

    Graph<T> build();

}
