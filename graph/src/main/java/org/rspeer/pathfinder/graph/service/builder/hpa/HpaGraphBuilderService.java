package org.rspeer.pathfinder.graph.service.builder.hpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rspeer.pathfinder.graph.model.graph.Graph;
import org.rspeer.pathfinder.graph.model.hpa.HpaGraph;
import org.rspeer.pathfinder.graph.model.hpa.HpaNode;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.Region;
import org.rspeer.pathfinder.graph.service.RegionFlagsService;
import org.rspeer.pathfinder.graph.service.builder.IGraphBuilderService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Service
@Slf4j
public class HpaGraphBuilderService implements IGraphBuilderService<HpaNode> {

    private static final int HIGHEST_REGION_SIZE = 640;

    private final RegionFlagsService regionFlagsService;
    private final AsyncHpaGraphBuilderService graphBuilderAsync;

    public Graph<HpaNode> build() {
        long ms = System.currentTimeMillis();
        regionFlagsService.loadAllIntoMemory();
        Map<Integer, HpaNode> rootNodes = buildRegions();
        HpaGraph graph = new HpaGraph(rootNodes);
        log.info("Generating internal nodes for {} root nodes", rootNodes.size());
        rootNodes.values().stream().map(node -> graphBuilderAsync.addConnections(node, graph)).forEach(it -> {
            try {
                it.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        log.info("Added {} nodes in {}ms", graphBuilderAsync.getAtomicNodes(), System.currentTimeMillis() - ms);
        return graph;
    }

    private Map<Integer, HpaNode> buildRegions() {
        Map<Integer, HpaNode> nodes = new HashMap<>();
        Rectangle rect = regionFlagsService.getBounds();
        for (int level = 0; level < Region.Z; level++) {
            for (int x = 0; x < rect.width; x += HIGHEST_REGION_SIZE) {
                for (int y = 0; y < rect.height; y += HIGHEST_REGION_SIZE) {
                    HpaNode node = new HpaNode(new Position(x + rect.x, y + rect.y, level), HIGHEST_REGION_SIZE, HIGHEST_REGION_SIZE);
                    nodes.put(node.hashCode(), node);
                    buildChildren(node);
                }
            }
        }

        return nodes;
    }

    private void buildChildren(HpaNode node) {
        int x = node.getRoot().getX();
        int y = node.getRoot().getY();
        int level = node.getRoot().getLevel();
        for (int xd = 0; xd < HIGHEST_REGION_SIZE; xd += HIGHEST_REGION_SIZE / 10) {
            for (int yd = 0; yd < HIGHEST_REGION_SIZE; yd += HIGHEST_REGION_SIZE / 10) {
                HpaNode child = new HpaNode(new Position(x + xd, y + yd, level), HIGHEST_REGION_SIZE / 10, HIGHEST_REGION_SIZE / 10);
                node.addChild(child);
            }
        }
    }

}
