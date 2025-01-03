package com.cgvsu.io.animationreader;

import com.cgvsu.animation.Frame;
import com.cgvsu.animation.ModelAnimation;
import com.cgvsu.animation.State;
import com.cgvsu.io.Vector3Deserializer;
import com.cgvsu.model.ModelPrepared;
import com.cgvsu.nmath.Vector3f;
import com.google.gson.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AnimationReader {
    public static Map<ModelPrepared, ModelAnimation> readAnimations(Map<String, ModelPrepared> models, String path) throws IOException {
        Path filename = Path.of(path);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Vector3f.class, new Vector3Deserializer())
                .registerTypeAdapter(State.class, new StateDeserializer())
                .registerTypeAdapter(Frame.class, new FrameDeserializer())
                .registerTypeAdapter(ModelAnimation.class, new ModelAnimationDeserializer())
                .create();

        String json = Files.readString(filename, StandardCharsets.UTF_8);
        JsonObject object = gson.fromJson(json, JsonObject.class);

        Map<ModelPrepared, ModelAnimation> result = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()){
            ModelPrepared model = models.get(entry.getKey());
            ModelAnimation animation = gson.fromJson(entry.getValue(), ModelAnimation.class);
            result.put(model, animation);
        }

        return result;
    }
}
