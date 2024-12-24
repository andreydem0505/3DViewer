package com.cgvsu.animation;

import com.cgvsu.Configs;

public class Frame {
    private final State initialState;
    private final State destinationState;
    private final long duration;
    transient private long lasted = 0;

    public Frame(State initialState, State destinationState, long duration) {
        this.initialState = initialState;
        this.destinationState = destinationState;
        this.duration = duration;
    }

    protected State next() {
        lasted += Configs.FRAME_TIME;
        return initialState.interpolate(destinationState, Math.min(1, (float) lasted / duration));
    }

    protected boolean isOver() {
        return lasted >= duration;
    }

    public State getInitialState() {
        return initialState;
    }

    public State getDestinationState() {
        return destinationState;
    }

    public long getDuration() {
        return duration;
    }
}
