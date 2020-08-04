package org.rspeer.pathfinder.graph.model.hpa;

import org.rspeer.pathfinder.graph.model.graph.Graph;
import org.rspeer.pathfinder.graph.model.rs.Position;

import java.util.*;

public class HpaGraph implements Graph {

    private final Map<Integer, HpaNode> nodes;

    public HpaGraph(Map<Integer, HpaNode> rootNodes) {
        this.nodes = rootNodes;
    }

    public HpaNode getInternalNode(Position position, int size) {
        return getInternalNode(position, size, nodes.values());
    }

    public List<HpaNode> getLeafNodesIn(Position base, int wOccurences, int hOccurences, int regionSize) {
        List<HpaNode> regions = new ArrayList<>();
        for (int x = 0; x < wOccurences; x++) {
            for (int y = 0; y < hOccurences; y++) {
                regions.add(getInternalNode(base.translate(x * regionSize, y * regionSize), regionSize));
            }
        }

        List<HpaNode> leafs = new ArrayList<>();
        for (HpaNode region : regions) {
            if (region == null) continue;
            leafs.addAll(region.getChildren());
        }

        return leafs;
    }

    private HpaNode getInternalNode(Position position, int size, Collection<HpaNode> nodes) {
        for (HpaNode node : nodes) {
            if (!node.contains(position) && !node.getRoot().equals(position)) {
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
}
