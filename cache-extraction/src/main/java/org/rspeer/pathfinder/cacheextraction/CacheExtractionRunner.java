package org.rspeer.pathfinder.cacheextraction;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.Cache;
import net.runelite.cache.MapImageDumper;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.fs.Store;
import net.runelite.cache.region.Region;
import net.runelite.cache.region.RegionLoader;
import org.rspeer.pathfinder.configuration.Configuration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheExtractionRunner implements ApplicationRunner {

    private static final File STORE_LOCATION = Paths.get(System.getProperty("user.home"), "jagexcache",
            "oldschool", "LIVE").toFile();

    private final AsyncCacheExtractor asyncCacheExtractor;

    @Override
    public void run(ApplicationArguments args) {
        try {
            Gson gson = new Gson();
            Store store = Cache.loadStore(STORE_LOCATION.toString());
            dumpObjects(store, gson);
            dumpRegions(store, gson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dumpObjects(Store store, Gson gson) throws IOException {
        Path objectsPath = Configuration.Locations.SCENE_ENTITY_DIR;
        if (!Files.exists(objectsPath)) {
            Files.createDirectories(objectsPath);
        }

        store.load();

        ObjectManager objectManager = new ObjectManager(store);
        objectManager.load();

        for (ObjectDefinition objectDefinition : objectManager.getObjects()) {
            asyncCacheExtractor.serializeObjectDefinition(objectDefinition, gson, objectsPath);
        }
    }

    private void dumpRegions(Store store, Gson gson) throws IOException {
        Path regionsPath = Configuration.Locations.REGION_DIR;
        if (!Files.exists(regionsPath)) {
            Files.createDirectories(regionsPath);
        }

        Path imgPath = Configuration.Locations.MAP_IMAGE_DIR;
        if (!Files.exists(imgPath)) {
            Files.createDirectories(imgPath);
        }

        store.load();

        MapImageDumper imageDumper = new MapImageDumper(store);
        imageDumper.load();

        RegionLoader regionLoader = new RegionLoader(store);
        regionLoader.loadRegions();
        for (Region region : regionLoader.getRegions()) {
            asyncCacheExtractor.serializeRegion(region, gson, regionsPath);
            asyncCacheExtractor.dumpMapImage(region, imageDumper, imgPath);
        }
    }

    private void dumpMapImages(Store store) throws IOException {
        Path imgPath = Configuration.Locations.MAP_IMAGE_DIR;
        if (!Files.exists(imgPath)) {
            Files.createDirectories(imgPath);
        }

        store.load();
        MapImageDumper imageDumper = new MapImageDumper(store);
        imageDumper.load();

    }

}
