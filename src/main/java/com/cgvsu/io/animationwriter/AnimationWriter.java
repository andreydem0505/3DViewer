package com.cgvsu.io.animationwriter;

import com.cgvsu.animation.Frame;
import com.cgvsu.animation.ModelAnimation;
import com.cgvsu.animation.State;
import com.cgvsu.io.Vector3Serializer;
import com.cgvsu.model.ModelPrepared;
import com.cgvsu.nmath.Vector3f;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class AnimationWriter {
    public static void writeAnimations(Map<ModelPrepared, ModelAnimation> animations, String path) throws IOException {
        Path fileName = Path.of(path);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Vector3f.class, new Vector3Serializer())
                .registerTypeAdapter(State.class, new StateSerializer())
                .registerTypeAdapter(Frame.class, new FrameSerializer())
                .registerTypeAdapter(ModelAnimation.class, new ModelAnimationSerializer())
                .create();

        JsonObject result = new JsonObject();

        for (Map.Entry<ModelPrepared, ModelAnimation> anim : animations.entrySet()){
            ModelPrepared modelPrepared = anim.getKey();
            ModelAnimation modelAnimation = anim.getValue();

            result.add(modelPrepared.getName(), gson.toJsonTree(modelAnimation));
        }

        Files.writeString(fileName, gson.toJson(result), StandardCharsets.UTF_8);
    }
}
