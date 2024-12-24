package com.cgvsu.io.animationreader;

import com.cgvsu.animation.State;
import com.cgvsu.nmath.Vector3f;
import com.google.gson.*;

import java.lang.reflect.Type;

public class StateDeserializer implements JsonDeserializer<State> {
    @Override
    public State deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        Vector3f position = context.deserialize(object.get("position"), Vector3f.class);
        Vector3f rotation = context.deserialize(object.get("rotation"), Vector3f.class);
        Vector3f scale = context.deserialize(object.get("scale"), Vector3f.class);

        State state = new State(position, rotation, scale);
        return state;
    }
}
