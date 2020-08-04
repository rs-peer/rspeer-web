package org.rspeer.pathfinder.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "org.rspeer.pathfinder.graph.service", "org.rspeer.pathfinder.generator", "org.rspeer.pathfinder.graph.algorithm"
        }
)
public class GeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeneratorApplication.class, args);
    }

}
