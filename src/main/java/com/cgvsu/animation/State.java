package com.cgvsu.animation;

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
        return new State(
                new Vector3f(
                        position.x() + (another.position.x() - position.x()) * t,
                        position.y() + (another.position.y() - position.y()) * t,
                        position.z() + (another.position.z() - position.z()) * t
                ),
                new Vector3f(
                        rotation.x() + (another.rotation.x() - rotation.x()) * t,
                        rotation.y() + (another.rotation.y() - rotation.y()) * t,
                        rotation.z() + (another.rotation.z() - rotation.z()) * t
                ),
                new Vector3f(
                        scale.x() + (another.scale.x() - scale.x()) * t,
                        scale.y() + (another.scale.y() - scale.y()) * t,
                        scale.z() + (another.scale.z() - scale.z()) * t
                )
        );
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
