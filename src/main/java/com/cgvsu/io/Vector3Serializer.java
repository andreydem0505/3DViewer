package com.cgvsu.io;

import com.cgvsu.nmath.Vector3f;
import com.google.gson.*;

import java.lang.reflect.Type;

public class Vector3Serializer implements JsonSerializer<Vector3f> {
    @Override
    public JsonElement serialize(Vector3f src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        array.add(src.x());
        array.add(src.y());
        array.add(src.z());

        return array;
    }
}
