package org.rspeer.pathfinder.graph.model.rs;

import java.util.function.Function;

public class CollectiveFlagsProvider implements Function<Position, Integer> {

    private final RegionFlags[] regionFlags;

    public CollectiveFlagsProvider(RegionFlags... regionFlags) {
        this.regionFlags = regionFlags;
    }

    @Override
    public Integer apply(Position position) {
        for (RegionFlags flags : this.regionFlags) {
            if (flags.contains(position)) {
                return flags.getFlagRebase(position);
            }
        }

        return null;
    }

}
