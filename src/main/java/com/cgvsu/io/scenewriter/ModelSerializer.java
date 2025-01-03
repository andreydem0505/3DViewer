package com.cgvsu.io.scenewriter;

import com.cgvsu.model.Model;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ModelSerializer implements JsonSerializer<Model> {
    @Override
    public JsonElement serialize(Model src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("position", context.serialize(src.position));
        object.add("rotation", context.serialize(src.rotation));
        object.add("scale", context.serialize(src.scale));
        return object;
    }
}
