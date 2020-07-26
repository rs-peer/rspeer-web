package org.rspeer.pathfinder.graph.model.rs;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class Position {

    private int x;

    private int y;

    private int level;

    public Position() {
    }

    public Position(int x, int y, int level) {
        this.x = x;
        this.y = y;
        this.level = level;
    }

    public Position translate(int modulus) {
        int x = this.x / modulus * modulus;
        int y = this.y / modulus * modulus;
        return new Position(x, y, this.level);
    }

    public Position translate(int dx, int dy) {
        return new Position()
                .setX(this.x + dx)
                .setY(this.y + dy)
                .setLevel(this.level);
    }

    public Position translate(int dx, int dy, int level) {
        return new Position()
                .setX(this.x + dx)
                .setY(this.y + dy)
                .setLevel(this.level + level);
    }

    public List<Position> getNeighbouringPositions(Predicate<Position> filter) {
        return Stream.of(
                translate(-1, 0),
                translate(1, 0),
                translate(0, 1),
                translate(0, -1)
        ).filter(filter).collect(Collectors.toList());
    }
}
