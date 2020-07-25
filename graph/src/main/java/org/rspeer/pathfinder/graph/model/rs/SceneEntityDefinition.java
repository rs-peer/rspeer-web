package org.rspeer.pathfinder.graph.model.rs;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SceneEntityDefinition {

    private Integer key;

    private Integer mapDoorFlag;
    private Integer identifier;
    private Integer ambient;
    private Integer ambientSoundId;
    private Integer animation;
    private Integer clipType;
    private Integer contrast;
    private Integer itemSupport;
    private Integer mapFunction;
    private Integer mapSceneId;
    private Integer scaleX;
    private Integer scaleY;
    private Integer scaleZ;
    private Integer sizeX;
    private Integer sizeY;
    private Integer translateX;
    private Integer translateY;
    private Integer translateZ;
    private Integer varpIndex;
    private Integer varpbitIndex;
    private String name;
    private Boolean clipped;
    private Boolean impenetrable;
    private Boolean projectileClipped;
    private Boolean rotated;
    private Boolean solid;
    private Integer[] transformIds;
    private String[] actions;
    private Integer[] colors;
    private Integer[] newColors;
    private Integer[] newTextures;
    private Integer[] textures;
    private Integer id;
}
