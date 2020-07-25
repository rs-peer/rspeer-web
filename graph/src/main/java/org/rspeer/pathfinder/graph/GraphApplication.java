package org.rspeer.pathfinder.graph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.serialize.PositionDeserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class GraphApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphApplication.class, args);
    }

    @Bean
    public Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Position.class, new PositionDeserializer())
                .create();
    }
}
