package com.cgvsu.animation;

import com.cgvsu.model.Model;
import com.cgvsu.model.ModelPrepared;

import java.util.HashMap;
import java.util.Map;

public class AnimationController {
    public Map<ModelPrepared, ModelAnimation> animations;

    public AnimationController() {
        animations = new HashMap<>();
    }

    public void animate() {
        for (Map.Entry<ModelPrepared, ModelAnimation> entry : animations.entrySet()) {
            State state = entry.getValue().animate();
            if (state != null)
                entry.getKey().model.setState(state);
        }
    }

    public boolean isOver() {
        boolean over = true;
        for (ModelAnimation animation : animations.values()) {
            over &= animation.isOver();
        }
        return over && !animations.isEmpty();
    }
}
