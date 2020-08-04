package org.rspeer.pathfinder.graph.util;

import org.rspeer.pathfinder.graph.model.rs.CollectiveFlagsProvider;
import org.rspeer.pathfinder.graph.model.rs.Direction;
import org.rspeer.pathfinder.graph.model.rs.Position;

import java.lang.reflect.Field;
import java.util.StringJoiner;
import java.util.function.Predicate;

public class MapFlags {

    public static final int OPEN_SETTINGS = 1;

    public static final int WALL_NORTH = 1 << 1;
    public static final int WALL_EAST = 1 << 2;
    public static final int WALL_SOUTH = 1 << 3;
    public static final int WALL_WEST = 1 << 4;
    public static final int WALL_TYPE_1 = 1 << 17;

    public static final int WALL_NORTH_EAST_TO_SOUTH_WEST = 1 << 5;
    public static final int WALL_NORTH_WEST_TO_SOUTH_EAST = 1 << 6;

    public static final int BLOCKED_SETTING = 1 << 7;
    public static final int BLOCKED_ROOF = 1 << 8;
    public static final int BLOCKED_SCENE_OBJECT = 1 << 16;
    public static final int BLOCKED_22 = 1 << 11;

    public static final int DOOR_FLAG = 1 << 9;

    public static final int OCCUPIED = 1 << 10;

    public static final int PILLAR_NORTH_WEST = 1 << 12;
    public static final int PILLAR_NORTH_EAST = 1 << 13;
    public static final int PILLAR_SOUTH_WEST = 1 << 14;
    public static final int PILLAR_SOUTH_EAST = 1 << 15;

    public static final int OPEN_OVERRIDE = 1 << 21;
    public static final int OPEN_OVERRIDE_START = 1 << 18;
    public static final int OPEN_OVERRIDE_END = 1 << 20;

    public static final int NO_OVERLAY = 1 << 19;

    public static final int PLANE_CHANGE_UP = 1 << 22;
    public static final int PLANE_CHANGE_DOWN = 1 << 23;

    public static int add(int flag, int value) {
        return flag | value;
    }

    public static int remove(int flag, int value) {
        return flag & value;
    }

    public static boolean check(int flag, int value) {
        return (flag & value) != 0;
    }

    public static boolean isBlocked(int flag) {
        return check(flag, BLOCKED_22 | BLOCKED_SETTING | BLOCKED_ROOF | BLOCKED_SCENE_OBJECT);
    }

    public static Predicate<Position> isWalkableFrom(Position from, CollectiveFlagsProvider flagsProvider, boolean ignoreStartBlocked, boolean ignoreDoors) {
        return to -> {
            Direction between = getDirectionBetween(from, to);
            Integer fromFlag = flagsProvider.apply(from);
            Integer toFlag = flagsProvider.apply(to);
            if (fromFlag == null || toFlag == null) {
                return false;
            }

            if (check(DOOR_FLAG, fromFlag) && ignoreDoors) {
                fromFlag = OPEN_SETTINGS;
            }

            if (check(DOOR_FLAG, toFlag) && ignoreDoors) {
                toFlag = OPEN_SETTINGS;
            }

            return checkWalkable(between, fromFlag, toFlag, ignoreStartBlocked);
        };
    }

    public static boolean checkWalkable(Direction dir, int startFlag, int endFLag, boolean ignoreStartBlocked) {

        if (isBlocked(endFLag) || (!ignoreStartBlocked && isBlocked(startFlag))) {
            return false;
        }

        switch (dir) {
            case NORTH:
                if (check(startFlag, WALL_NORTH)) {
                    return false;
                }
                break;
            case SOUTH:
                if (check(startFlag, WALL_SOUTH)) {
                    return false;
                }
                break;
            case WEST:
                if (check(startFlag, WALL_WEST)) {
                    return false;
                }
                break;
            case EAST:
                if (check(startFlag, WALL_EAST)) {
                    return false;
                }
                break;
        }
        return true;
    }

    public static Direction getDirectionBetween(Position from, Position to) {
        Position diff = to.translate(-from.getX(), -from.getY());
        for (Direction dir : Direction.values()) {
            if (dir.getXOff() == diff.getX() && dir.getYOff() == diff.getY()) {
                return dir;
            }
        }

        return null;
    }

    public static String toString(int flag) {
        StringJoiner result = new StringJoiner(" ");

        for (Field field : MapFlags.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                int checkFlag = (int) field.get(null);
                if (check(flag, checkFlag)) {
                    result.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }
}
