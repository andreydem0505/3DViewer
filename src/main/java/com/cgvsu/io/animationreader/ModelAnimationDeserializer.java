package com.cgvsu.io.animationreader;

import com.cgvsu.animation.Frame;
import com.cgvsu.animation.ModelAnimation;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ModelAnimationDeserializer implements JsonDeserializer<ModelAnimation> {
    @Override
    public ModelAnimation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();

        ModelAnimation animation = new ModelAnimation();
        for (int i = 0; i < array.size(); i++){
            animation.addFrame(context.deserialize(array.get(i), Frame.class));
        }

        return animation;
    }
}
