package org.rspeer.pathfinder.graph.model.rs;

public enum Direction {

    NORTH(2, 0, 1, true),
    NORTH_EAST(0, 1, 1, false),
    EAST(1, 1, 0, true),
    SOUTH_EAST(0, 1, -1, false),
    SOUTH(8, 0, -1, true),
    SOUTH_WEST(0, -1, -1, false),
    WEST(4, -1, 0, true),
    NORTH_WEST(0, -1, 1, false);

    int orientation;
    int xOff;
    int yOff;
    private boolean cardinal;

    Direction(int orientation, int xOff, int yOff, boolean cardinal) {
        this.orientation = orientation;
        this.xOff = xOff;
        this.yOff = yOff;
        this.cardinal = cardinal;
    }

    public int getXOff() {
        return xOff;
    }

    public int getYOff() {
        return yOff;
    }

    public int getOrientation() {
        return orientation;
    }

    public boolean isSameAxis(int orientation) {
        if (orientation == getOrientation()) {
            return true;
        }

        if (this == NORTH && SOUTH.getOrientation() == orientation) {
            return true;
        }
        if (this == SOUTH && NORTH.getOrientation() == orientation) {
            return true;
        }

        if (this == EAST && WEST.getOrientation() == orientation) {
            return true;
        }

        return this == WEST && EAST.getOrientation() == orientation;
    }

    public boolean isCardinal() {
        return cardinal;
    }

    public Direction reverse() {
        switch (this) {
            case EAST:
                return WEST;

            case WEST:
                return EAST;

            case NORTH:
                return SOUTH;

            default:
                return NORTH;

        }
    }
}