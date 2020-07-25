package org.rspeer.pathfinder.graph.service.builder.hpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rspeer.pathfinder.graph.model.hpa.HpaGraph;
import org.rspeer.pathfinder.graph.model.hpa.HpaNode;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.Region;
import org.rspeer.pathfinder.graph.service.RegionFlagsService;
import org.rspeer.pathfinder.graph.service.builder.IGraphBuilderService;
import org.rspeer.pathfinder.graph.util.MapFlags;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Service
@Slf4j
public class HpaGraphBuilderService implements IGraphBuilderService {

    private static final int HIGHEST_REGION_SIZE = 640;

    private final RegionFlagsService regionFlagsService;

    public HpaGraph build() {
        regionFlagsService.loadAllIntoMemory();
        Map<Integer, HpaNode> rootNodes = buildRegions();
        HpaGraph graph = new HpaGraph(rootNodes);
        log.info("Generating internal nodes for {} root nodes", rootNodes.size());
        rootNodes.values().forEach(node -> {
            log.info("Working on root node at {}", node.getRoot());
            addStairsConnections(node, graph);
            addExternalConnections(node, graph);
        });

        return graph;
    }

    @Async("runtimePool")
    protected void addExternalConnections(HpaNode node, HpaGraph graph) {

    }

    @Async("runtimePool")
    protected void addStairsConnections(HpaNode node, HpaGraph graph) {
        // This node cannot have subnodes
        if (node.getWidth() == 1) {
            return;
        }

        // This is not a root level node
        if (node.getChildren().size() > 0) {
            for (HpaNode child : node.getChildren()) {
                addStairsConnections(child, graph);
            }

            return;
        }

        int level = node.getRoot().getLevel();
        for (int x = 0; x < node.getWidth(); x++) {
            for (int y = 0; y < node.getHeight(); y++) {
                Position trans = node.getRoot().translate(x, y);
                int flag = regionFlagsService.getFlagFor(trans);
                if (flag == -1) continue;

                // Edges are external since different planes are different regions
                if (MapFlags.check(flag, MapFlags.PLANE_CHANGE_UP)) {
                    if (level + 1 >= Region.Z) continue;
                    Position upPos = trans.translate(0, 0, 1);
                    HpaNode upRegion = graph.getInternalNode(upPos, node.getWidth());
                    HpaNode up = node.computeIfAbsent(trans);
                    HpaNode upNode = upRegion.computeIfAbsent(upPos);
                    up.addExternalEdge(upNode, false);
                }

                if (MapFlags.check(flag, MapFlags.PLANE_CHANGE_DOWN)) {
                    if (level - 1 < 0) continue;
                    HpaNode down = node.computeIfAbsent(trans);
                    Position downPos = trans.translate(0, 0, -1);
                    HpaNode downRegion = graph.getInternalNode(downPos, node.getWidth());
                    HpaNode downNode = downRegion.computeIfAbsent(downPos);
                    down.addExternalEdge(downNode, false);
                }
            }
        }
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
