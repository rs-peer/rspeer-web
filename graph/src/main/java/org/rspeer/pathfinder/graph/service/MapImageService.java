package org.rspeer.pathfinder.graph.service;

import org.rspeer.pathfinder.configuration.Configuration;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MapImageService {

    private final Map<String, BufferedImage> imageMap = new HashMap<>();

    private String getFor(int region, int level) {
        return String.format("%d-%d", region, level);
    }

    public BufferedImage get(int region, int level) {
        return imageMap.computeIfAbsent(getFor(region, level), key -> getFromCache(region, level));
    }

    public BufferedImage getFromCache(int region, int level) {
        File file = Configuration.Locations.MAP_IMAGE_DIR.resolve(String.format("%s.png", getFor(region, level)))
                .toFile();
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
