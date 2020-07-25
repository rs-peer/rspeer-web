package org.rspeer.pathfinder.graph.model.rs;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LocationObject {

    private int id;
    private int type;
    private int orientation;
    private Position position;

}
