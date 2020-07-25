package org.rspeer.pathfinder.graph.util;

import org.rspeer.pathfinder.graph.model.rs.Position;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class GenerationData {

    private final Set<Position> blockedCache = new HashSet<>();

    public void addPosition(int x, int y, int level) {
        blockedCache.add(new Position()
                .setX(x)
                .setY(y)
                .setLevel(level)
        );
    }

    public boolean blockedCacheContains(int x, int y, int level) {
        return blockedCache.contains(new Position(x, y, level));
    }
}
