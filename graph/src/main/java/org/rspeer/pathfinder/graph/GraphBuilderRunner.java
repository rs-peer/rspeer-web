package org.rspeer.pathfinder.graph;

import lombok.AllArgsConstructor;
import org.rspeer.pathfinder.graph.service.builder.hpa.HpaGraphBuilderService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class GraphBuilderRunner implements ApplicationRunner {

    private final HpaGraphBuilderService graphBuilderService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        graphBuilderService.build();
    }
}
