package org.rspeer.pathfinder.cacheextraction;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.Cache;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.fs.Store;
import net.runelite.cache.region.Region;
import net.runelite.cache.region.RegionLoader;
import org.rspeer.pathfinder.configuration.Configuration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class CacheExtractionRunner implements ApplicationRunner {

    private static final File STORE_LOCATION = Paths.get(System.getProperty("user.home"), "jagexcache",
            "oldschool", "LIVE").toFile();

    @Override
    public void run(ApplicationArguments args) {
        try {
            Gson gson = new Gson();
            Store store = Cache.loadStore(STORE_LOCATION.toString());
            dumpRegions(store, gson);
            dumpObjects(store, gson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dumpObjects(Store store, Gson gson) throws IOException {
        Path objectsPath = Configuration.Locations.SCENE_ENTITY_DIR;
        if (!Files.exists(objectsPath)) {
            Files.createDirectories(objectsPath);
        }

        ObjectManager objectManager = new ObjectManager(store);
        objectManager.load();
        for (ObjectDefinition objectDefinition : objectManager.getObjects()) {
            serializeObjectDefinition(objectDefinition, gson, objectsPath);
        }
    }

    private void dumpRegions(Store store, Gson gson) throws IOException {
        Path regionsPath = Configuration.Locations.REGION_DIR;
        if (!Files.exists(regionsPath)) {
            Files.createDirectories(regionsPath);
        }

        RegionLoader regionLoader = new RegionLoader(store);
        regionLoader.loadRegions();
        for (Region region : regionLoader.getRegions()) {
            serializeRegion(region, gson, regionsPath);
        }
    }

    @Async("runtimePool")
    public void serializeObjectDefinition(ObjectDefinition objectDefinition, Gson gson, Path base) throws IOException {
        File objectFile = base.resolve(String.format("%d.json", objectDefinition.getId())).toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(objectFile))) {
            log.info("Object {} serialized", objectDefinition.getId());
            gson.toJson(objectDefinition, writer);
        }
    }

    @Async("runtimePool")
    public void serializeRegion(Region region, Gson gson, Path base) throws IOException {
        File regionFile = base.resolve(region.getRegionID() + ".json").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(regionFile))) {
            log.info("Region {} serialized", region.getRegionID());
            gson.toJson(region, writer);
        }
    }

}
