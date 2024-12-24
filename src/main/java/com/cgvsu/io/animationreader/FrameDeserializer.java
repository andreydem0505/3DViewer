package com.cgvsu.io.animationreader;

import com.cgvsu.animation.Frame;
import com.cgvsu.animation.State;
import com.google.gson.*;

import java.lang.reflect.Type;

public class FrameDeserializer implements JsonDeserializer<Frame> {
    @Override
    public Frame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = new JsonObject();

        State initialState = context.deserialize(object.get("initialState"), State.class);
        State destinationState = context.deserialize(object.get("destinationState"), State.class);
        long duration = object.get("duration").getAsLong();

        return new Frame(initialState, destinationState, duration);
    }
}
