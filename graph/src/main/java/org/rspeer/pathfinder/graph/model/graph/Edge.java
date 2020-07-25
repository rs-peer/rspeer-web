package org.rspeer.pathfinder.graph.model.graph;

public interface Edge {

    Node getStart();

    Node getEnd();

    double getCost();

}
