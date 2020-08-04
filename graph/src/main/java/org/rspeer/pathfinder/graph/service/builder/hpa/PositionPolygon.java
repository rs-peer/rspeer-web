package org.rspeer.pathfinder.graph.service.builder.hpa;

import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.RegionFlags;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PositionPolygon {

    public static final int MAX_POL_SIZE = 14;

    private final Position root;
    private final Set<Position> positions = new HashSet<>();
    private final Set<PositionPolygon> edges = new HashSet<>();
    private int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

    public PositionPolygon(Position root) {
        this.root = root;
        add(root);
    }

    public void add(Position position) {
        this.positions.add(position);
        this.minX = Math.min(position.getX(), minX);
        this.minY = Math.min(position.getY(), minY);
        this.maxX = Math.max(position.getX(), maxX);
        this.maxY = Math.max(position.getY(), maxY);
    }

    public int getWidth() {
        return this.maxX - this.minX;
    }

    public int getHeight() {
        return this.maxY - this.minY;
    }

    public boolean isFull() {
        return getWidth() >= MAX_POL_SIZE || getHeight() >= MAX_POL_SIZE;
    }

    public int size() {
        return positions.size();
    }

    public boolean contains(Position position) {
        return positions.contains(position);
    }

    public void addEdge(PositionPolygon to) {
        if (to == this) {
            return;
        }

        this.edges.add(to);
        to.edges.add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionPolygon that = (PositionPolygon) o;
        return Objects.equals(root, that.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }

    public Set<PositionPolygon> getEdges() {
        return this.edges;
    }

    public Position getRoot() {
        return root;
    }
}
