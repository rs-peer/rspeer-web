package org.rspeer.pathfinder.graph.service.builder.hpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rspeer.pathfinder.graph.algorithm.pathing.local.dijkstra.DijkstraAlgorithm;
import org.rspeer.pathfinder.graph.model.hpa.HpaGraph;
import org.rspeer.pathfinder.graph.model.hpa.HpaNode;
import org.rspeer.pathfinder.graph.model.rs.*;
import org.rspeer.pathfinder.graph.service.RegionFlagsService;
import org.rspeer.pathfinder.graph.util.MapFlags;
import org.rspeer.pathfinder.graph.util.ObservableAtomicValue;
import org.rspeer.pathfinder.graph.util.Tuple;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncHpaGraphBuilderService {

    private final DijkstraAlgorithm dijkstraAlgorithm;
    private final RegionFlagsService regionFlagsService;
    private final ObservableAtomicValue<Integer> atomicNodes = new ObservableAtomicValue<>(0);
    private final ObservableAtomicValue<Integer> atomicEdges = new ObservableAtomicValue<>(0);

    private void atomicOrBranch(HpaNode node, Consumer<HpaNode> branch) {
        if (node.getChildren().size() == 0 && !node.isLeaf()) {
            branch.accept(node);
        }

        for (HpaNode child : node.getChildren()) {
            if (child.isLeaf()) {
                branch.accept(node);
                break;
            } else {
                atomicOrBranch(child, branch);
            }
        }
    }

    @PostConstruct
    protected void initialize() {
        log.info("Initialized generator");
        atomicNodes.addListener(val -> {
            if (val % 10000 == 0) {
                log.info("Added 10k nodes, total {}", val);
            }
        });

        atomicEdges.addListener(val -> {
            if (val % 10000 == 0) {
                log.info("Added 10k edges, total {}", val);
            }
        });
    }

    @Async
    protected ListenableFuture<Void> addAllInternal(HpaNode node, HpaGraph graph) {
        atomicOrBranch(node, n2 -> {
            addStairsConnections(n2, graph);
            addInternalConnections(n2);
        });

        return new AsyncResult<>(null);
    }

    @Async
    protected ListenableFuture<Void> addAllExternal(HpaNode node, HpaGraph graph) {
        atomicOrBranch(node, n2 -> {
            addExternalConnections(n2, graph);
        });

        return new AsyncResult<>(null);
    }

    @Async
    public void prune(HpaNode parent) {
        parent.getChildren().removeIf(node -> node.outgoing().isEmpty() && node.incoming().isEmpty());
        parent.getChildren().forEach(this::prune);
    }

    protected void addStairsConnections(HpaNode node, HpaGraph graph) {
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
                    log.debug("Adding positive plane change from {} to {}", trans, upPos);
                    atomicNodes.update(value -> value++);
                    atomicEdges.update(value -> value + 1);
                }

                if (MapFlags.check(flag, MapFlags.PLANE_CHANGE_DOWN)) {
                    if (level - 1 < 0) continue;
                    HpaNode down = node.computeIfAbsent(trans);
                    Position downPos = trans.translate(0, 0, -1);
                    HpaNode downRegion = graph.getInternalNode(downPos, node.getWidth());
                    HpaNode downNode = downRegion.computeIfAbsent(downPos);
                    down.addExternalEdge(downNode, false);
                    log.debug("Adding negative plane change from {} to {}", trans, downPos);
                    atomicNodes.update(value -> value++);
                    atomicEdges.update(value -> value + 1);
                }
            }
        }
    }

    protected void addInternalConnections(HpaNode node) {
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
        CollectiveFlagsProvider cfp = new CollectiveFlagsProvider(flags);
        BiPredicate<Position, Position> walkableTest = (from, to) -> MapFlags.isWalkableFrom(from, cfp, true, true).test(to);

        for (Position from : unblocked) {
            if (polygonCache.containsKey(from)) continue;
            PositionPolygon positionPolygon = createPolygon(from, polygonCache, unblocked, flags);
            polygonSet.add(positionPolygon);
        }

        Map<PositionPolygon, HpaNode> polygonMap = polygonSet.stream()
                .collect(Collectors.toMap(pol -> pol, pol -> new HpaNode(pol.getRoot(), 1, 1)));

        for (PositionPolygon from : polygonMap.keySet()) {
            for (PositionPolygon to : from.getEdges()) {
                polygonMap.get(from).addEdge(polygonMap.get(to), false);
            }
        }

        for (PositionPolygon polygon : polygonSet) {
            HpaNode polygonNode = polygonMap.get(polygon);
            node.addChild(polygonNode);
            //polygonSet.stream().filter(p ->
            //        dijkstraAlgorithm.getDistanceEvaluator().distance(polygon.getRoot(), p.getRoot()) < 18
            //).forEach(neighbour -> {
            //    HpaNode neighbourNode = polygonMap.get(neighbour);
            //    if (neighbourNode == null) return;
            //    if (dijkstraAlgorithm.isReachable(polygon.getRoot(), neighbour.getRoot(),
            //            walkableTest, 18)) {
            //        polygonNode.addEdge(neighbourNode, false);
            //        log.debug("Added edge from {} to {}", polygonNode.getRoot(), neighbourNode.getRoot());
            //    }
            //});

            var filtered = polygonSet.stream().filter(p ->
                    dijkstraAlgorithm.getDistanceEvaluator().distance(polygon.getRoot(), p.getRoot()) < 18
            ).collect(Collectors.toMap(PositionPolygon::getRoot, Function.identity()));

            Map<Position, Integer> score = new HashMap<>();
            score.put(polygon.getRoot(), 0);
            //using basic queue since priorityqueue.remove is heavy, and we dont need to always pick the lowest cost neighbor next in this case
            Queue<Position> visiting = new LinkedList<>();
            visiting.add(polygon.getRoot());

            while (!visiting.isEmpty()) {
                Position current = visiting.remove();
                var existing = filtered.get(current);
                if (existing != null && score.getOrDefault(current, Integer.MAX_VALUE) <= 18) {
                    var neighbourNode = polygonMap.get(existing);
                    polygonNode.addEdge(neighbourNode, false);
                    if (polygon.getEdges().size() == filtered.size()) {
                        break;
                    }
                }

                int currentScore = score.get(current);
                if (currentScore > 18) {
                    continue;
                }

                for (Position neighbour : current.getNeighbouringPositions(nb -> walkableTest.test(current, nb))) {
                    int newScore = currentScore + 1;
                    int neighbourScore = score.getOrDefault(neighbour, Integer.MAX_VALUE);
                    if (newScore < neighbourScore) {
                        score.put(neighbour, newScore);
                        visiting.add(neighbour);
                    }
                }
            }
        }

        atomicNodes.update(prev -> prev += polygonMap.size());
    }

    private PositionPolygon createPolygon(Position from, Map<Position, PositionPolygon> polygonCache,
                                          Set<Position> unblocked, RegionFlags flags) {
        BiPredicate<Position, Position> walkableTest = (f, t) -> MapFlags
                .isWalkableFrom(f, new CollectiveFlagsProvider(flags), false, false)
                .test(t);

        PositionPolygon currentPolygon = new PositionPolygon(from);
        PriorityQueue<Position> visiting = new PriorityQueue<>(
                Comparator.comparingDouble(p -> dijkstraAlgorithm.getDistanceEvaluator().distance(from, p))
        );

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
                for (Position pos : current.getNeighbouringPositions(pos -> flags.contains(pos) && !checking.contains(pos))) {
                    if (!walkableTest.test(current, pos)) continue;
                    visiting.add(pos);
                }
            }
        }

        return currentPolygon;
    }

    @Async
    protected void addExternalConnections(HpaNode node, HpaGraph graph) {
        Position root = node.getRoot();
        RegionFlags flags = regionFlagsService.getFor(root);
        if (flags == null) {
            return;
        }

        HpaNode west = graph.getInternalNode(root.translate(-1, 0), node.getWidth());
        HpaNode south = graph.getInternalNode(root.translate(0, -1), node.getWidth());
        LinkedList<Tuple<HpaNode>> all = getNClosest(node, west, Direction.WEST);
        all.addAll(getNClosest(node, south, Direction.SOUTH));

        if (all.isEmpty() && node.getChildren().size() > 0) {
            log.debug("Cannot connect {} to south or west", node.getRoot());
        }

        for (Tuple<HpaNode> tuple : all) {
            atomicEdges.update(value -> value + 1);
            tuple.getFirst().addExternalEdge(tuple.getSecond(), true);
            log.debug("Added edge from {} to {}", tuple.getFirst().getRoot(), tuple.getSecond().getRoot());
        }
    }

    private LinkedList<Tuple<HpaNode>> getNClosest(HpaNode from, HpaNode to, Direction in) {
        if (to == null) {
            return new LinkedList<>();
        }

        RegionFlags fromFlags = regionFlagsService.getFor(from.getRoot());
        RegionFlags toFlags = regionFlagsService.getFor(to.getRoot());
        if (fromFlags == null || toFlags == null) {
            return new LinkedList<>();
        }

        CollectiveFlagsProvider cvf = new CollectiveFlagsProvider(fromFlags, toFlags);
        LinkedList<Tuple<HpaNode>> tuples = new LinkedList<>();
        for (HpaNode fromEntry : from.getChildren()) {
            HpaNode closestToEntry = null;
            //double closestDist = Double.MAX_VALUE;
            //for (HpaNode toEntry : to.getChildren()) {
            //    double dist = dijkstraAlgorithm.getDistanceEvaluator().distance(from.getRoot(), toEntry.getRoot());
            //    if (dist > closestDist) continue;
            //    boolean isReachable = dijkstraAlgorithm.isReachable(fromEntry.getRoot(), toEntry.getRoot(),
            //            (p1, p2) -> MapFlags.isWalkableFrom(p1, cvf, true, true).test(p2), 24);
            //    if (isReachable) {
            //        closestDist = dist;
            //        closestToEntry = toEntry;
            //    }
            //}

            Map<Position, Integer> score = new HashMap<>();
            score.put(fromEntry.getRoot(), 0);
            Queue<Position> visiting = new PriorityQueue<>(Comparator.comparingDouble(p -> score.getOrDefault(p, Integer.MAX_VALUE)));
            visiting.add(fromEntry.getRoot());
            var toChildren = to.getChildren().stream().collect(Collectors.toMap(HpaNode::getRoot, Function.identity()));

            while (!visiting.isEmpty()) {
                Position current = visiting.remove();
                var existing = toChildren.get(current);
                if (existing != null && score.getOrDefault(current, Integer.MAX_VALUE) <= 24) {
                    closestToEntry = existing;
                    break;
                }

                int currentScore = score.get(current);
                if (currentScore > 24) {
                    continue;
                }

                for (Position neighbour : current.getNeighbouringPositions(nb -> MapFlags.isWalkableFrom(current, cvf, true, true).test(nb))) {
                    int newScore = currentScore + 1;
                    int neighbourScore = score.getOrDefault(neighbour, Integer.MAX_VALUE);
                    if (newScore < neighbourScore) {
                        score.put(neighbour, newScore);
                        visiting.add(neighbour);
                    }
                }
            }

            if (closestToEntry != null) {
                tuples.add(new Tuple<>(fromEntry, closestToEntry));
            }
        }

        return tuples;
    }

    private List<HpaNode> getNClosest(HpaNode from, Direction in) {
        Comparator<HpaNode> comparator;
        switch (in) {
            case NORTH:
                comparator = Comparator.comparingInt(HpaNode::getY).reversed();
                break;

            case EAST:
                comparator = Comparator.comparingInt(HpaNode::getX).reversed();
                break;

            case WEST:
                comparator = Comparator.comparingInt(HpaNode::getX);
                break;

            case SOUTH:
                comparator = Comparator.comparingInt(HpaNode::getY);
                break;

            default:
                return Collections.emptyList();
        }

        List<HpaNode> sorted = new ArrayList<>(from.getChildren());
        sorted.sort(comparator);

        double dist = 0;
        HpaNode prev = null;
        List<HpaNode> result = new ArrayList<>();
        for (HpaNode sortedEl : sorted) {
            if (dist > Region.X) {
                break;
            }

            if (prev != null) {
                double currDist = dijkstraAlgorithm.getDistanceEvaluator().distance(sortedEl.getRoot(), prev.getRoot());
                dist += currDist;
                result.add(sortedEl);
            }

            prev = sortedEl;
        }

        return result;
    }

    protected int getAtomicNodes() {
        return atomicNodes.get();
    }
}
