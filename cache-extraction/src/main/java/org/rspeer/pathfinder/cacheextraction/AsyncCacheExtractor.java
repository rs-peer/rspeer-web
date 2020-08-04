package org.rspeer.pathfinder.cacheextraction;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.MapImageDumper;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Region;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

@Service
@Slf4j
public class AsyncCacheExtractor {

    @Async
    public void serializeObjectDefinition(ObjectDefinition objectDefinition, Gson gson, Path base) throws IOException {
        File objectFile = base.resolve(String.format("%d.json", objectDefinition.getId())).toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(objectFile))) {
            log.debug("Object {} serialized", objectDefinition.getId());
            gson.toJson(objectDefinition, writer);
        }
    }

    @Async
    public void serializeRegion(Region region, Gson gson, Path base) throws IOException {
        File regionFile = base.resolve(region.getRegionID() + ".json").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(regionFile))) {
            log.debug("Region {} serialized", region.getRegionID());
            gson.toJson(region, writer);
        }
    }

    @Async
    public void dumpMapImage(Region region, MapImageDumper imageDumper, Path base) throws IOException {
        for (int level = 0; level < 4; level++) {
            BufferedImage levelImg = imageDumper.drawRegion(region, level);
            ImageIO.write(levelImg, "png",
                    base.resolve(String.format("%d-%d.png", region.getRegionID(), level)).toFile());
        }
    }

}
