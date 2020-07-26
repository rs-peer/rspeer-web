package org.rspeer.pathfinder.graph.service;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.rspeer.pathfinder.configuration.Configuration;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.RegionFlags;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Service
public class RegionFlagsService {

    private final Gson gson;
    private final RegionService regionService;
    private final Map<Integer, RegionFlags> regionFlagsCache = new HashMap<>();

    private Rectangle bounds;

    public int getFlagFor(Position position) {
        RegionFlags region = getFor(position);
        if (region == null) {
            return -1;
        }

        int localX = position.getX() - region.getBaseX();
        int localY = position.getY() - region.getBaseY();
        return region.getFlags()[position.getLevel()][localX][localY];
    }

    public RegionFlags getFor(int id) {
        return regionFlagsCache.computeIfAbsent(id, this::getFromCache);
    }

    public RegionFlags getFor(Position position) {
        int id = regionService.locationToRegionId(position.getX(), position.getY());
        return getFor(id);
    }

    private RegionFlags getFromCache(int id) {
        Path file = Configuration.Locations.FLAGS_DIR.resolve(String.format("%d.json", id));
        if (!Files.exists(file)) {
            return null;
        }

        return getFromCache(file);
    }

    private RegionFlags getFromCache(Path file) {
        try {
            return gson.fromJson(Configuration.getPathReader(file), RegionFlags.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void loadAllIntoMemory() {
        try {
            Files.walk(Configuration.Locations.FLAGS_DIR)
                    .filter(file -> Files.isRegularFile(file))
                    .map(this::getFromCache)
                    .filter(Objects::nonNull)
                    .forEach(flags -> regionFlagsCache.put(flags.getId(), flags));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Rectangle computeBounds() {
        int minBaseX = Integer.MAX_VALUE, minBaseY = Integer.MAX_VALUE,
                maxBaseX = Integer.MIN_VALUE, maxBaseY = Integer.MIN_VALUE;

        for (RegionFlags flags : regionFlagsCache.values()) {
            minBaseX = Math.min(minBaseX, flags.getBaseX());
            minBaseY = Math.min(minBaseY, flags.getBaseY());
            maxBaseX = Math.max(maxBaseX, flags.getBaseX());
            maxBaseY = Math.max(maxBaseY, flags.getBaseY());
        }

        return new Rectangle(minBaseX, minBaseY, (maxBaseX - minBaseX), (maxBaseY - minBaseY));
    }

    public Rectangle getBounds() {
        if (this.bounds == null) {
            this.bounds = computeBounds();
        }

        return this.bounds;
    }
}
