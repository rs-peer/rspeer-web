package org.rspeer.pathfinder.graph.model.rs.serialize;

import com.google.gson.*;
import org.rspeer.pathfinder.graph.model.rs.Position;

import java.lang.reflect.Type;

public class PositionDeserializer implements JsonDeserializer<Position> {

    @Override
    public Position deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        int x = object.get("x").getAsInt();
        int y = object.get("y").getAsInt();
        int level;
        if (object.has("z")) {
            level = object.get("z").getAsInt();
        } else {
            level = object.get("level").getAsInt();
        }

        return new Position(x, y, level);
    }

}
