package org.rspeer.pathfinder.cacheextraction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
public class CacheExtractionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheExtractionApplication.class, args);
    }

}
