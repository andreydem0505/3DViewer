package com.cgvsu.animation;

import com.cgvsu.Configs;

public class Frame {
    private State initialState;
    private State destinationState;
    private long duration;
    private long lasted = 0;

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

    protected void reset() {
        lasted = 0;
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
    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public void setDestinationState(State destinationState) {
        this.destinationState = destinationState;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
