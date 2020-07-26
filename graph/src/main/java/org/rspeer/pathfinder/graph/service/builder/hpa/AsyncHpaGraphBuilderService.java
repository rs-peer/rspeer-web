package org.rspeer.pathfinder.graph.service.builder.hpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rspeer.pathfinder.graph.model.hpa.HpaGraph;
import org.rspeer.pathfinder.graph.model.hpa.HpaNode;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.Region;
import org.rspeer.pathfinder.graph.model.rs.RegionFlags;
import org.rspeer.pathfinder.graph.service.RegionFlagsService;
import org.rspeer.pathfinder.graph.util.MapFlags;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncHpaGraphBuilderService {

    private final RegionFlagsService regionFlagsService;
    private final AtomicInteger atomicNodes = new AtomicInteger(0);

    private boolean atomicOrBranch(HpaNode node, Consumer<HpaNode> branch) {
        boolean result = false;
        for (HpaNode child : node.getChildren()) {
            if (!child.isLeaf()) {
                branch.accept(child);
                result = true;
            }
        }

        return result;
    }


    public int getAtomicNodes() {
        return atomicNodes.get();
    }

    @Async
    protected ListenableFuture<Void> addConnections(HpaNode node, HpaGraph graph) {
        atomicOrBranch(node, n2 -> {
            addStairsConnections(n2, graph);
            addInternalConnections(n2);
        });

        return new AsyncResult<>(null);
    }

    protected void addStairsConnections(HpaNode node, HpaGraph graph) {
        if (atomicOrBranch(node, n2 -> addStairsConnections(n2, graph))) {
            return;
        }

        RegionFlags flags = regionFlagsService.getFor(node.getRoot());
        if (flags == null) {
            return;
        }

        int level = node.getRoot().getLevel();
        for (int x = 0; x < node.getWidth(); x++) {
            for (int y = 0; y < node.getHeight(); y++) {
                Position trans = node.getRoot().translate(x, y);
                int flag = flags.getFlag(trans.translate(-flags.getBaseX(), -flags.getBaseY()));
                if (flag == -1) continue;

                // Edges are external since different planes are different regions
                if (MapFlags.check(flag, MapFlags.PLANE_CHANGE_UP)) {
                    if (level + 1 >= Region.Z) continue;
                    Position upPos = trans.translate(0, 0, 1);
                    HpaNode upRegion = graph.getInternalNode(upPos, node.getWidth());
                    HpaNode up = node.computeIfAbsent(trans);
                    HpaNode upNode = upRegion.computeIfAbsent(upPos);
                    up.addExternalEdge(upNode, false);
                    log.info("Adding positive plane change from {} to {}", trans, upPos);
                    atomicNodes.getAndUpdate(value -> value += 2);
                }

                if (MapFlags.check(flag, MapFlags.PLANE_CHANGE_DOWN)) {
                    if (level - 1 < 0) continue;
                    HpaNode down = node.computeIfAbsent(trans);
                    Position downPos = trans.translate(0, 0, -1);
                    HpaNode downRegion = graph.getInternalNode(downPos, node.getWidth());
                    HpaNode downNode = downRegion.computeIfAbsent(downPos);
                    down.addExternalEdge(downNode, false);
                    log.info("Adding negative plane change from {} to {}", trans, downPos);
                    atomicNodes.getAndUpdate(value -> value += 2);
                }
            }
        }
    }

    protected void addInternalConnections(HpaNode node) {
        if (atomicOrBranch(node, this::addInternalConnections)) {
            return;
        }

        long ms = System.currentTimeMillis();

        // Because our root are alligned up with regions we only have to get flags once
        Position root = node.getRoot();
        RegionFlags flags = regionFlagsService.getFor(root);
        if (flags == null) {
            //We don't have data here
            return;
        }

        Position base = root.translate(-flags.getBaseX(), -flags.getBaseY());
        Set<Position> unblocked = new HashSet<>();
        for (int x = 0; x < node.getWidth() - 1; x++) {
            for (int y = 0; y < node.getHeight() - 1; y++) {
                Position blockedTrans = base.translate(x, y);
                if (!MapFlags.isBlocked(flags.getFlag(blockedTrans))) {
                    unblocked.add(blockedTrans.translate(flags.getBaseX(), flags.getBaseY()));
                }
            }
        }

        if (unblocked.isEmpty()) {
            // Entire region is blocked
            return;
        }

        Set<PositionPolygon> polygonSet = new HashSet<>();
        Map<Position, PositionPolygon> polygonCache = new HashMap<>();

        for (Position from : unblocked) {
            if (polygonCache.containsKey(from)) continue;
            polygonSet.add(createPolygon(from, polygonCache, unblocked, flags));
        }

        Map<PositionPolygon, HpaNode> polygonMap = polygonSet.stream()
                .collect(Collectors.toMap(pol -> pol, pol -> new HpaNode(pol.getCentroid(), 1, 1)));
        for (PositionPolygon positionPolygon : polygonMap.keySet()) {
            HpaNode polygonNode = polygonMap.get(positionPolygon);
            node.addChild(polygonNode);
            for (PositionPolygon neighbourPolygon : positionPolygon.getEdges()) {
                HpaNode neighbourNode = polygonMap.get(neighbourPolygon);
                polygonNode.addEdge(neighbourNode, false);
            }
        }

        atomicNodes.getAndUpdate(prev -> prev += polygonMap.size());
    }

    private PositionPolygon createPolygon(Position from, Map<Position, PositionPolygon> polygonCache,
                                          Set<Position> unblocked, RegionFlags flags) {
        PositionPolygon currentPolygon = new PositionPolygon(from);
        Queue<Position> visiting = new LinkedList<>();
        visiting.add(from);

        while (!visiting.isEmpty() && !currentPolygon.isFull()) {
            Position current = visiting.remove();
            if (polygonCache.containsKey(current)) {
                polygonCache.get(current).addEdge(currentPolygon);
                continue;
            }

            if (unblocked.contains(current)) {
                currentPolygon.add(current);
                polygonCache.put(current, currentPolygon);

                final PositionPolygon checking = currentPolygon;
                visiting.addAll(current.getNeighbouringPositions(pos -> flags.contains(pos) && !checking.contains(pos)));
            }

        }

        return currentPolygon;
    }

    @Async
    protected void addExternalConnections(HpaNode node, HpaGraph graph) {
        if (atomicOrBranch(node, n2 -> addExternalConnections(n2, graph))) {
            return;
        }


    }

    private void addExternalConnections(HpaNode node, HpaGraph graph, int side) {
        for (int x = 0; x < node.getWidth(); x++) {
            Position crr = node.getRoot().translate(x, 0);
            Position external = crr.translate(0, 1);
        }
    }
}
