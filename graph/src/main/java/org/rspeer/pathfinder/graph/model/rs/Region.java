package org.rspeer.pathfinder.graph.model.rs;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Region {

    public static final int X = 64;
    public static final int Y = 64;
    public static final int Z = 4;

    private int regionID;
    private int baseX;
    private int baseY;

    private int[][][] tileHeights = new int[Z][X][Y];
    private int[][][] tileSettings = new int[Z][X][Y];
    private int[][][] overlayIds = new int[Z][X][Y];
    private int[][][] overlayPaths = new int[Z][X][Y];
    private int[][][] overlayRotations = new int[Z][X][Y];
    private int[][][] underlayIds = new int[Z][X][Y];
    private List<LocationObject> locations = new ArrayList<>();

    private int[] keys;

    public int getAt(int[][][] map, Position position) {
        Position translated = position.translate(-baseX, -baseY);
        return map[translated.getLevel()][translated.getX()][translated.getY()];
    }

    public int getTileSetting(Position location) {
        return getAt(tileSettings, location);
    }
}
