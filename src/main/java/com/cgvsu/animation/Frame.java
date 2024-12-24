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

    protected State next() {
        lasted += Configs.FRAME_TIME;
        return initialState.interpolate(destinationState, Math.min(1, (float) lasted / duration));
    }

    protected boolean isOver() {
        return lasted >= duration;
    }
}
