package com.cgvsu.animation;

import com.cgvsu.math.Linal;
import com.cgvsu.nmath.Vector3f;

public class State {
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;

    public State(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    protected State interpolate(State another, float t) {
        Vector3f pos = Linal.interpolateVector(this.position, another.position, t);
        Vector3f rot = Linal.interpolateVector(this.rotation, another.rotation, t);
        Vector3f sca = Linal.interpolateVector(this.scale, another.scale, t);
        return new State(pos, rot, sca);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }
}
