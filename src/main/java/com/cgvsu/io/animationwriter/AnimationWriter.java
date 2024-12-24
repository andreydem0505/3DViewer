package com.cgvsu.io.animationwriter;

import com.cgvsu.animation.Frame;
import com.cgvsu.animation.ModelAnimation;
import com.cgvsu.animation.State;
import com.cgvsu.model.ModelPrepared;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.util.Map;

public class AnimationWriter {
    public static void writeAnimationController(Map<ModelPrepared, ModelAnimation> animations) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        for (Map.Entry<ModelPrepared, ModelAnimation> anim : animations.entrySet()){
            ModelPrepared modelPrepared = anim.getKey();
            ModelAnimation modelAnimation = anim.getValue();

            JsonElement fileName = gson.toJsonTree(modelPrepared.getName());
            for (Frame frame : modelAnimation.getFrames()){

            }
        }
//        System.out.println("a");
    }


    public static void main(String[] args) {
        Gson gson = new Gson();
    }
}
