package org.rspeer.pathfinder.configuration;

import org.springframework.context.annotation.Bean;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Configuration {

    public static class Locations {

        public static final Path HOME_DIR = Paths.get(System.getProperty("user.home"), "pathfinding");
        public static final Path REGION_DIR = HOME_DIR.resolve("regions");
        public static final Path FLAGS_DIR = HOME_DIR.resolve("flags");
        public static final Path SCENE_ENTITY_DIR = HOME_DIR.resolve("scene-entities");
    }

    public static Reader getPathReader(Path path) throws FileNotFoundException {
        return new FileReader(path.toFile());
    }

    @Bean(name = "runtimePool")
    public Executor threadPoolTaskExecutor() {
        return Executors.newFixedThreadPool(40);
    }

}
