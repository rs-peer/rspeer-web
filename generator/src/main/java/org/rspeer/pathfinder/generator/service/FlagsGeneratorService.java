package org.rspeer.pathfinder.generator.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rspeer.pathfinder.configuration.Configuration;
import org.rspeer.pathfinder.graph.model.rs.*;
import org.rspeer.pathfinder.graph.service.RegionService;
import org.rspeer.pathfinder.graph.service.SceneEntityService;
import org.rspeer.pathfinder.graph.util.MapFlags;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@AllArgsConstructor
public class FlagsGeneratorService {

    private final Gson gson;
    private final RegionService regionService;
    private final SceneEntityService sceneEntityService;

    public void generateAllFlags() {
        try {
            if (!Files.isDirectory(Configuration.Locations.FLAGS_DIR)) {
                Files.createDirectories(Configuration.Locations.FLAGS_DIR);
            }

            Files.walk(Configuration.Locations.REGION_DIR)
                    .filter(file -> Files.isRegularFile(file))
                    .map(file -> regionService.getRegionFromCache(file.getFileName().toString()))
                    .map(this::generateFlagsFor)
                    .forEach(this::write);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(CompletableFuture<RegionFlags> future) {
        RegionFlags get = future.join();
        File file = Configuration.Locations.FLAGS_DIR.resolve(String.format("%d.json", get.getId())).toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            gson.toJson(get, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RegionFlags generateFlagsFor(int id) {
        Region region = regionService.getRegion(id);
        if (region == null) {
            return null;
        }

        return generateFlagsFor(region)
                .join();
    }

    @Async
    public CompletableFuture<RegionFlags> generateFlagsFor(Region region) {
        RegionFlags flags = new RegionFlags(region.getRegionID(), region.getBaseX(), region.getBaseY());
        for (int plane = 0; plane < Region.Z; plane++) {
            for (int regionX = 0; regionX < Region.X; regionX++) {
                for (int regionY = 0; regionY < Region.Y; regionY++) {
                    int setting = region.getTileSettings()[plane][regionX][regionY];
                    int underlay = region.getUnderlayIds()[plane][regionX][regionY];
                    int overlay = region.getOverlayIds()[plane][regionX][regionY];
                    int height = region.getTileHeights()[plane][regionX][regionY];
                    int rotation = region.getOverlayRotations()[plane][regionX][regionY];
                    int path = region.getOverlayPaths()[plane][regionX][regionY];

                    boolean bridge = plane + 1 < 4 && (region.getTileSettings()[plane + 1][regionX][regionY] == 2);
                    boolean isNull = setting == 0 && underlay == 0 && overlay == 0;// && height == 0; //&& rotation == 0 && path == 0;
                    if (!bridge && setting == 1 || isNull) {
                        //blocked
                        flags.addFlag(regionX, regionY, plane, MapFlags.BLOCKED_SETTING);
                    } else {
                        //walkable
                        flags.addFlag(regionX, regionY, plane, MapFlags.OPEN_SETTINGS);
                    }
                    if (overlay == 0) {
                        flags.addFlag(regionX, regionY, plane, MapFlags.NO_OVERLAY);
                    }
                }
            }
        }

        for (LocationObject location : region.getLocations()) {
            int locationType = location.getType();

            int baseX = region.getBaseX();
            int baseY = region.getBaseY();
            int regionX = location.getPosition().getX() - baseX;
            int regionY = location.getPosition().getY() - baseY;
            int plane = location.getPosition().getLevel();

            if (plane < 4) {
                if ((region.getTileSettings()[1][regionX][regionY] & 2) == 2) {//Settings that apply locations to plane below.
                    plane = plane - 1;
                }

                if (plane >= 0) {
                    flags.addFlag(location.getPosition(), MapFlags.OCCUPIED);

                    SceneEntityDefinition definition = sceneEntityService.get(location.getId());
                    if (definition.getMapFunction() == 5) {
                        //TODO: Save this as a bank
                        Map<String, Object> save = new HashMap<>();
                        save.put("x", baseX + regionX);
                        save.put("y", baseY + regionY);
                        save.put("plane", plane);
                        save.put("actions", definition.getActions());
                        save.put("name", definition.getName());
                        save.put("subKey", definition.getKey());
                    }

                    boolean isDoor = sceneEntityService.isDoor(definition);

                    if (isDoor) {
                        flags.addFlag(location.getPosition(), MapFlags.DOOR_FLAG);
                    }

                    Integer clipType = definition.getClipType();
                    if (locationType == 22) {
                        if (clipType == 1) {
                            //block22 Never actually happens but client checks it.
                            flags.addFlag(location.getPosition(), MapFlags.BLOCKED_22);
                        }
                    } else {
                        if (locationType != 10 && locationType != 11) {
                            int rotation = location.getOrientation();

                            if (locationType >= 12) {
                                if (clipType != 0) {
                                    //addObject block, these are visible roofs from other rooms.
                                    flags.addFlag(location.getPosition(), MapFlags.BLOCKED_ROOF);
                                }
                            }

                            if (!isDoor && definition.getItemSupport() == 1) {
                                if (locationType == 0 || locationType == 2) {
                                    if (rotation == 0) {
                                        //West wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_WEST);
                                    } else if (rotation == 1) {
                                        //North wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_NORTH);
                                    } else if (rotation == 2) {
                                        //East wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_EAST);
                                    } else if (rotation == 3) {
                                        //South wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_SOUTH);
                                    }
                                }

                                if (locationType == 1) {
                                    //Wall interconnecting ignore
                                    flags.addFlag(location.getPosition(), MapFlags.WALL_TYPE_1);
                                }

                                if (locationType == 2) {
                                    if (rotation == 3) {
                                        //West wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_WEST);
                                    } else if (rotation == 0) {
                                        //North wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_NORTH);
                                    } else if (rotation == 1) {
                                        //East wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_EAST);
                                    } else if (rotation == 2) {
                                        //South wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_SOUTH);
                                    }
                                }

                                if (locationType == 3) {
                                    if (rotation == 0) {
                                        //Pillar North-West
                                        flags.addFlag(location.getPosition(), MapFlags.PILLAR_NORTH_WEST);
                                    } else if (rotation == 1) {
                                        //Pillar North-East
                                        flags.addFlag(location.getPosition(), MapFlags.PILLAR_NORTH_EAST);
                                    } else if (rotation == 2) {
                                        //Pillar South-East
                                        flags.addFlag(location.getPosition(), MapFlags.PILLAR_SOUTH_EAST);
                                    } else if (rotation == 3) {
                                        //Pillar South-West
                                        flags.addFlag(location.getPosition(), MapFlags.PILLAR_SOUTH_WEST);
                                    }
                                }

                                if (locationType == 9) {
                                    int hash = (regionX << 7) + regionY + (location.getId() << 14) + 0x4000_0000;
                                    if ((hash >> 29 & 3) != 2) {
                                        continue; //Idk works
                                    }

                                    if (rotation != 0 && rotation != 2) {
                                        //North-West to South-East wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_NORTH_WEST_TO_SOUTH_EAST);
                                    } else {
                                        //North-East to South-West wall
                                        flags.addFlag(location.getPosition(), MapFlags.WALL_NORTH_EAST_TO_SOUTH_WEST);
                                    }
                                }
                            }

                            if (locationType == 4) {
                                //addBoundaryDecoration ignore
                            } else {
                                if (locationType == 5) {
                                    //addBoundaryDecoration ignore
                                } else if (locationType == 6) {
                                    //addBoundaryDecoration ignore
                                } else if (locationType == 7) {
                                    //addBoundaryDecoration ignore
                                } else if (locationType == 8) {
                                    //addBoundaryDecoration ignore
                                }
                            }
                        } else {
                            if (definition.getItemSupport() == 1) {
                                //addObject blocks walking

                                int width;
                                int length;

                                int orientation = location.getOrientation();
                                if (orientation != 1 && orientation != 3) {
                                    width = definition.getSizeX();
                                    length = definition.getSizeY();
                                } else {
                                    width = definition.getSizeY();
                                    length = definition.getSizeX();
                                }

                                boolean override = false;

                                SceneEntityService.PlaneChange planeChange = sceneEntityService.getPlaneChange(definition);
                                boolean up = planeChange == SceneEntityService.PlaneChange.UP || planeChange == SceneEntityService.PlaneChange.BOTH;
                                boolean down = planeChange == SceneEntityService.PlaneChange.DOWN || planeChange == SceneEntityService.PlaneChange.BOTH;

                                for (int xOff = 0; xOff < width; xOff++) {
                                    for (int yOff = 0; yOff < length; yOff++) {
                                        Position translated = location.getPosition().translate(xOff, yOff);
                                        flags.addFlag(translated, MapFlags.BLOCKED_SCENE_OBJECT);

                                        if (override)
                                            flags.addFlag(translated, MapFlags.OPEN_OVERRIDE);

                                        if (up || down) {
                                            flags.addFlag(translated, MapFlags.OPEN_OVERRIDE_END);
                                            flags.addFlag(translated, MapFlags.OPEN_OVERRIDE_START);
                                        }
                                        if (up)
                                            flags.addFlag(translated, MapFlags.PLANE_CHANGE_UP);
                                        if (down)
                                            flags.addFlag(translated, MapFlags.PLANE_CHANGE_DOWN);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(flags);
    }

}
