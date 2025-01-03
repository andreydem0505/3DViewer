package com.cgvsu.io;

import com.cgvsu.nmath.Vector3f;
import com.google.gson.*;

import java.lang.reflect.Type;

public class Vector3Deserializer implements JsonDeserializer<Vector3f> {
    @Override
    public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();

        float x = array.get(0).getAsFloat();
        float y = array.get(1).getAsFloat();
        float z = array.get(2).getAsFloat();

        return new Vector3f(x, y, z);
    }
}
