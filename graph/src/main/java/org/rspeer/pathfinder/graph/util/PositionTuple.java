package org.rspeer.pathfinder.graph.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rspeer.pathfinder.graph.model.rs.Position;

@AllArgsConstructor
@Getter
public class PositionTuple {

    private final Position first;
    private final Position second;

}
