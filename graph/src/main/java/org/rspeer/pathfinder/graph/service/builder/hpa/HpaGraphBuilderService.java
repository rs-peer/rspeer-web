package org.rspeer.pathfinder.graph.service.builder.hpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rspeer.pathfinder.graph.model.hpa.HpaGraph;
import org.rspeer.pathfinder.graph.model.hpa.HpaNode;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.Region;
import org.rspeer.pathfinder.graph.service.RegionFlagsService;
import org.rspeer.pathfinder.graph.service.builder.IGraphBuilderService;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class HpaGraphBuilderService implements IGraphBuilderService {

    private static final int HIGHEST_REGION_SIZE = 320;

    private final RegionFlagsService regionFlagsService;
    private final AsyncHpaGraphBuilderService graphBuilderAsync;

    @Override
    public HpaGraph build() {
        long ms = System.currentTimeMillis();
        regionFlagsService.loadAllIntoMemory();
        Map<Integer, HpaNode> rootNodes = buildRegions();
        HpaGraph graph = new HpaGraph(rootNodes);
        log.info("Generating internal nodes for {} root nodes", rootNodes.size());
        Collection<ListenableFuture<Void>> futures = rootNodes.values().stream()
                .map(node -> graphBuilderAsync.addAllInternal(node, graph))
                .collect(Collectors.toList());

        for (ListenableFuture<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //graph.getNodes().forEach(graphBuilderAsync::prune);
        log.info("Generating external nodes for {} internal nodes", graphBuilderAsync.getAtomicNodes());
        futures = rootNodes.values().stream()
                .map(node -> graphBuilderAsync.addAllExternal(node, graph))
                .collect(Collectors.toList());
        for (ListenableFuture<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

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
        for (int xd = 0; xd < HIGHEST_REGION_SIZE; xd += HIGHEST_REGION_SIZE / 5) {
            for (int yd = 0; yd < HIGHEST_REGION_SIZE; yd += HIGHEST_REGION_SIZE / 5) {
                HpaNode child = new HpaNode(new Position(x + xd, y + yd, level), HIGHEST_REGION_SIZE / 5, HIGHEST_REGION_SIZE / 5);
                node.addChild(child);
            }
        }
    }

}
