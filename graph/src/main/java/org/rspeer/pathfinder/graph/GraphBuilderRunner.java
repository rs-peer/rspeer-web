package org.rspeer.pathfinder.graph;

import lombok.AllArgsConstructor;
import org.rspeer.pathfinder.graph.model.graph.Graph;
import org.rspeer.pathfinder.graph.model.hpa.HpaGraph;
import org.rspeer.pathfinder.graph.model.hpa.HpaNode;
import org.rspeer.pathfinder.graph.service.builder.IGraphBuilderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Component
public class GraphBuilderRunner implements CommandLineRunner {

    private final IGraphBuilderService<HpaNode> graphBuilderService;
    private final Executor executor;

    @Override
    public void run(String... args) throws Exception {
        Graph<HpaNode> graph = graphBuilderService.build();
        System.out.println(graph);
    }
}
