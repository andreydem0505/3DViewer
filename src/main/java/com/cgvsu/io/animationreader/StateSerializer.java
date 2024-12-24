package com.cgvsu.io.animationreader;

import com.cgvsu.animation.State;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class StateSerializer implements JsonSerializer<State> {
    @Override
    public JsonElement serialize(State src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("position", context.serialize(src.getPosition()));
        object.add("rotation", context.serialize(src.getRotation()));
        object.add("scale", context.serialize(src.getScale()));

        return object;
    }
}
