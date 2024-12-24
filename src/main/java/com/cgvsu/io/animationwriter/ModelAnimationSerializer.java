package com.cgvsu.io.animationwriter;

import com.cgvsu.animation.Frame;
import com.cgvsu.animation.ModelAnimation;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ModelAnimationSerializer implements JsonSerializer<ModelAnimation> {
    @Override
    public JsonElement serialize(ModelAnimation src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();

        for (Frame frame : src.getFrames())
            array.add(context.serialize(frame));

        return array;
    }
}
