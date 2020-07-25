package org.rspeer.pathfinder.graph.model.rs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.rspeer.pathfinder.graph.util.MapFlags;

@RequiredArgsConstructor
@Getter
public class RegionFlags {

    private final int id;

    private final Integer baseX, baseY;

    private final int[][][] flags = new int[Region.Z][Region.X][Region.Y];

    public void addFlag(int regionX, int regionY, int plane, int flag) {
        flags[plane][regionX][regionY] = MapFlags.add(flags[plane][regionX][regionY], flag);
    }

    public boolean checkFlag(int regionX, int regionY, int plane, int flag) {
        return MapFlags.check(flags[plane][regionX][regionY], flag);
    }

    public void addFlag(Position location, int flag) {
        int regionX = location.getX() - baseX;
        int regionY = location.getY() - baseY;
        if (regionX >= Region.X || regionX < 0 || regionY >= Region.Y || regionY < 0) {
            return;
        }

        addFlag(regionX, regionY, location.getLevel(), flag);
    }
}
