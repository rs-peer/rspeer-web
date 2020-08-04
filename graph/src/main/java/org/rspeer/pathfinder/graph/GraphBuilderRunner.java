package org.rspeer.pathfinder.graph;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rspeer.pathfinder.graph.algorithm.pathing.web.dijkstra.WebDijkstraAlgorithm;
import org.rspeer.pathfinder.graph.model.hpa.HpaGraph;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.service.MapImageService;
import org.rspeer.pathfinder.graph.service.RegionFlagsService;
import org.rspeer.pathfinder.graph.service.builder.IGraphBuilderService;
import org.rspeer.pathfinder.graph.ui.GraphPanel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.Scanner;

@AllArgsConstructor
@Component
@Slf4j
public class GraphBuilderRunner implements CommandLineRunner {

    private final IGraphBuilderService graphBuilderService;
    private final WebDijkstraAlgorithm webDijkstraAlgorithm;
    private final RegionFlagsService regionFlagsService;
    private final MapImageService mapImageService;

    @Override
    public void run(String... args) {
        HpaGraph graph = (HpaGraph) graphBuilderService.build();

        SwingUtilities.invokeLater(() -> {
            JFrame container = new JFrame();
            container.setContentPane(new GraphPanel(graph, regionFlagsService, mapImageService));
            container.pack();
            container.validate();
            container.setVisible(true);
        });

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            try {
                Position first = new Position()
                        .setX(scanner.nextInt())
                        .setY(scanner.nextInt())
                        .setLevel(scanner.nextInt());
                Position second = new Position()
                        .setX(scanner.nextInt())
                        .setY(scanner.nextInt())
                        .setLevel(scanner.nextInt());

                log.info("Building from {} to {}", first, second);
                webDijkstraAlgorithm.buildPath(first, second, graph);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
