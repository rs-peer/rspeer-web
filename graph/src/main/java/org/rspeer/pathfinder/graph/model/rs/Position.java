package org.rspeer.pathfinder.graph.model.rs;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

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
}
