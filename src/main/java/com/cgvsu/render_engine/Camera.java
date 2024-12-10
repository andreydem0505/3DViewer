package com.cgvsu.render_engine;

import com.cgvsu.math.Linal;
import com.cgvsu.nmath.Matrix4x4;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.nmath.Vector4f;

public class Camera {
    private Vector2f rotation;
    private float distance;
    private Vector3f target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;

    public Camera(
            final Vector2f rotation,
            final float distance,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.rotation = rotation;
        this.distance = distance;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
    }

    public Vector3f getTarget() {
        return target;
    }

    public Vector2f getRotation() {
        return rotation;
    }

    public void setRotation(Vector2f rotation) {
        this.rotation = rotation;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public void moveRotation(final Vector2f translation) {
        this.rotation.add(translation);
    }

    public void moveDistance(final float translation) {
        this.distance += translation;
    }

    public void moveTarget(final Vector3f translation) {
        this.target.add(translation);
    }

    public Vector3f getPosition() {
        Vector3f position = new Vector3f(target);
        position.add(new Vector3f(
                distance * Linal.sin(rotation.x()) * Linal.sin(rotation.y()),
                distance * Linal.cos(rotation.y()),
                distance * Linal.cos(rotation.x()) * Linal.sin(rotation.y())));
        return position;
    }

    Matrix4x4 getViewMatrix() {
        return GraphicConveyor.lookAt(getPosition(), target);
    }

    Matrix4x4 getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }
}