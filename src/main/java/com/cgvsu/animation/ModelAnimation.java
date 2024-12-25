package com.cgvsu.animation;

import java.util.ArrayList;
import java.util.List;

public class ModelAnimation {
    private final List<Frame> frames;

    private int currentFrame = 0;

    public ModelAnimation() {
        frames = new ArrayList<>();
    }

    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    protected State animate() {
        if (isOver()) return null;
        Frame frame = frames.get(currentFrame);
        State state = frame.next();
        if (frame.isOver()) currentFrame++;
        return state;
    }

    protected boolean isOver() {
        return currentFrame >= frames.size();
    }

    public List<Frame> getFrames() {
        return frames;
    }

    protected void reset() {
        for (Frame frame : frames) {
            frame.reset();
        }
        currentFrame = 0;
    }
}