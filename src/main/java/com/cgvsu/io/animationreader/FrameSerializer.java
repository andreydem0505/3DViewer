package com.cgvsu.io.animationreader;

import com.cgvsu.animation.Frame;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class FrameSerializer implements JsonSerializer<Frame> {
    @Override
    public JsonElement serialize(Frame src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("initialState", context.serialize(src.getInitialState()));
        object.add("destinationState", context.serialize(src.getDestinationState()));
        object.addProperty("duration", src.getDuration());

        return object;
    }
}
