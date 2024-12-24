package com.cgvsu.animation;

import com.cgvsu.Configs;

public class Frame {
    private final State initialState;
    private final State destinationState;
    private final long duration;
    private long lasted;

    public Frame(State initialState, State destinationState, long duration) {
        this.initialState = initialState;
        this.destinationState = destinationState;
        this.duration = duration;
        lasted = 0;
    }

    public State next() {
        lasted += Configs.FRAME_TIME;
        return initialState.interpolate(destinationState, (float) lasted / duration);
    }

    public boolean isOver() {
        return lasted >= duration;
    }
}
