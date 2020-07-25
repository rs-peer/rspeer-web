package org.rspeer.pathfinder.graph.model.hpa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.rspeer.pathfinder.graph.model.graph.Edge;
import org.rspeer.pathfinder.graph.model.graph.Node;
import org.rspeer.pathfinder.graph.model.rs.Position;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@ToString
public class HpaNode implements Node {

    @Getter(value = AccessLevel.PRIVATE)
    private final Set<Edge> outgoing = new HashSet<>();

    @Getter(value = AccessLevel.PRIVATE)
    private final Set<Edge> incoming = new HashSet<>();

    private final Set<HpaNode> children = new HashSet<>();
    private final Position root;
    private final int width;
    private final int height;
    private HpaNode parent;

    public static int hashCode(Position root, int width, int height) {
        return Objects.hash(root, width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HpaNode node = (HpaNode) o;
        return width == node.width &&
                height == node.height &&
                root.equals(node.root);
    }

    public void setParent(HpaNode parent) {
        this.parent = parent;
    }

    public int getX() {
        return root.getX();
    }

    public int getY() {
        return root.getY();
    }

    public int getLevel() {
        return root.getLevel();
    }

    public boolean contains(Position child) {
        if (getWidth() == 1 || child.getLevel() != this.getLevel()) {
            return false;
        }

        int x = root.getX();
        int y = root.getY();
        return child.getX() >= x && child.getX() < x + getWidth()
                && child.getY() >= y && child.getY() < y + getHeight();
    }

    public boolean contains(HpaNode child) {
        return contains(child.getRoot());
    }

    public void addChild(HpaNode toAdd) {
        if (getWidth() == 1) {
            throw new RuntimeException("Cannot add children to a leaf node");
        }

        for (HpaNode child : getChildren()) {
            if (child.contains(toAdd)) {
                child.addChild(toAdd);
                return;
            }
        }

        if (this.children.contains(toAdd)) {
            return;
        }

        this.children.add(toAdd);
        toAdd.setParent(this);
    }

    public void addExternalEdge(HpaNode to, boolean bidirectional) {
        addEdge(to, bidirectional);

        if (this.getParent() != null
                && to.getParent() != null) {
            this.getParent().addExternalEdge(to.getParent(), bidirectional);
        }
    }

    public void addEdge(HpaNode to, boolean bidirectional) {
        HpaEdge edge = new HpaEdge(this, to);
        this.outgoing.add(edge);
        to.incoming.add(edge);

        if (bidirectional) {
            HpaEdge in = new HpaEdge(to, this);
            this.incoming.add(in);
            to.outgoing.add(in);
        }
    }

    public HpaNode computeIfAbsent(Position root) {
        HpaNode computed = new HpaNode(root, 1, 1);
        addChild(computed);
        return computed;
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public boolean isLeaf() {
        return getChildren().size() == 0;
    }

    @Override
    public int hashCode() {
        return hashCode(root, width, height);
    }

    @Override
    public Set<Edge> outgoing() {
        return outgoing;
    }

    @Override
    public Set<Edge> incoming() {
        return incoming;
    }
}
