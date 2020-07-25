package org.rspeer.pathfinder.graph.service;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rspeer.pathfinder.configuration.Configuration;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.Region;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
public class RegionService {

    private final Map<Integer, Region> regions = new HashMap<>();
    private final Gson gson;
    private final SceneEntityService sceneEntityService;

    public int locationToRegionId(int worldX, int worldY) {
        worldX >>>= 6;
        worldY >>>= 6;
        return (worldX << 8) | worldY;
    }

    public Position regionIdToBase(int regionId) {
        return new Position(((regionId >> 8) & 0xFF) << 6, (regionId & 0xFF) << 6, 0);
    }

    public Position locationToRegionBase(Position location) {
        if (location == null) return null;
        int regionId = locationToRegionId(location.getX(), location.getY());
        return regionIdToBase(regionId);
    }

    public Region getRegion(int id) {
        return regions.computeIfAbsent(id, this::getRegionFromCache);
    }

    public Region getRegionFromCache(String fileName) {
        Path location = Configuration.Locations.REGION_DIR.resolve(fileName);
        try {
            return gson.fromJson(Configuration.getPathReader(location), Region.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Region getRegionFromCache(int id) {
        return getRegionFromCache(String.format("%d.json", id));
    }

}
