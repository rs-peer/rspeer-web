package org.rspeer.pathfinder.graph.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.rspeer.pathfinder.configuration.Configuration;
import org.rspeer.pathfinder.graph.model.rs.SceneEntityDefinition;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class SceneEntityService {

    private static final String OPEN_DOOR_STRING = "open";
    private static final List<String> DOOR_NAMES = Arrays.asList(
            "door", "gate", "large door"
    );
    private static final List<String> PLANE_CHANGE_NAMES = Arrays.asList(
            "ladder", "stairs", "staircase", "stairwell"
    );

    private final Gson gson;
    private final Map<Integer, SceneEntityDefinition> entityCache = new HashMap<>();

    public SceneEntityDefinition get(int id) {
        return entityCache.computeIfAbsent(id, this::loadFromCache);
    }

    public SceneEntityDefinition loadFromCache(int id) {
        Path file = Configuration.Locations.SCENE_ENTITY_DIR.resolve(String.format("%d.json", id));
        try {
            return gson.fromJson(Configuration.getPathReader(file), SceneEntityDefinition.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isDoor(SceneEntityDefinition definition) {
        if (checkNonnullActions(definition, OPEN_DOOR_STRING::equalsIgnoreCase)) {
            return false;
        }

        if (definition.getName() == null || !DOOR_NAMES.contains(definition.getName().toLowerCase())) {
            return false;
        }

        return definition.getMapDoorFlag() != 0;
    }

    public boolean isPlaneChange(SceneEntityDefinition definition) {
        if (definition.getName() == null) {
            return false;
        }

        return PLANE_CHANGE_NAMES.contains(definition.getName());
    }

    public PlaneChange getPlaneChange(SceneEntityDefinition definition) {
        if (!isPlaneChange(definition)) {
            return null;
        }

        boolean up = checkNonnullActions(definition, action -> action.toLowerCase().contains("up"));
        boolean down = checkNonnullActions(definition, action -> action.toLowerCase().contains("down"));
        if (up && down) {
            return PlaneChange.BOTH;
        } else if (up) {
            return PlaneChange.UP;
        } else if (down) {
            return PlaneChange.DOWN;
        } else {
            return null;
        }
    }

    private boolean checkNonnullActions(SceneEntityDefinition definition, Predicate<String> tester) {
        if (definition.getActions() == null) {
            return false;
        }

        return Stream.of(definition.getActions())
                .filter(Objects::nonNull)
                .anyMatch(tester);
    }

    public enum PlaneChange {
        UP, DOWN, BOTH
    }
}
