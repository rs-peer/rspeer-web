package org.rspeer.pathfinder.graph.model.hpa;

import org.rspeer.pathfinder.graph.model.graph.Graph;
import org.rspeer.pathfinder.graph.model.rs.Position;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class HpaGraph implements Graph<HpaNode> {

    private final Map<Integer, HpaNode> nodes;

    public HpaGraph(Map<Integer, HpaNode> rootNodes) {
        this.nodes = rootNodes;
    }

    public HpaNode getInternalNode(Position position, int size) {
        return getInternalNode(position, size, nodes.values());
    }

    private HpaNode getInternalNode(Position position, int size, Collection<HpaNode> nodes) {
        for (HpaNode node : nodes) {
            if (!node.contains(position)) {
                continue;
            }

            if (node.getWidth() == size) {
                return node;
            } else {
                return getInternalNode(position, size, node.getChildren());
            }
        }

        return null;
    }

    @Override
    public Collection<HpaNode> getNodes() {
        return nodes.values();
    }

    @Override
    public Set<HpaNode> buildPath(Position from, Position to) {
        return null;
    }
}
