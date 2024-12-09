package com.cgvsu.model;

import com.cgvsu.math.Linal;
import com.cgvsu.nmath.Matrix4x4;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;

import java.util.*;

public class Model {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    public Model() {
        position = new Vector3f(0f, 0f, 0f);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1f, 1f, 1f);
    }

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();

    public Matrix4x4 getModelMatrix() {
        return getTransformMatrix()
                .multiplyMM(getRotateMatrix())
                .multiplyMM(getScaleMatrix());
    }

    private Matrix4x4 getScaleMatrix() {
        return GraphicConveyor.scale(scale.x(), scale.y(), scale.z());
    }

    private Matrix4x4 getRotateMatrix() {
        return GraphicConveyor.rotate(rotation.x(), rotation.y(), rotation.z());
    }

    private Matrix4x4 getTransformMatrix() {
        return GraphicConveyor.transform(position.x(), position.y(), position.z());
    }
}
