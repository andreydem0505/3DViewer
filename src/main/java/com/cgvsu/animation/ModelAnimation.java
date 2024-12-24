package com.cgvsu.animation;

import java.util.ArrayList;
import java.util.List;

public class ModelAnimation {
    private final List<Frame> frames;
    private int currentFrame;

    public ModelAnimation() {
        frames = new ArrayList<>();
        currentFrame = 0;
    }

    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    public State animate() {
        if (isOver()) return null;
        Frame frame = frames.get(currentFrame);
        State state = frame.next();
        if (frame.isOver()) currentFrame++;
        return state;
    }

    public boolean isOver() {
        return currentFrame >= frames.size();
    }
}