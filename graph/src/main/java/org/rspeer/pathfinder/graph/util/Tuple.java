package org.rspeer.pathfinder.graph.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rspeer.pathfinder.graph.model.rs.Position;

@AllArgsConstructor
@Getter
public class Tuple<T> {

    private final T first;
    private final T second;

}
