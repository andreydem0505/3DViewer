package com.cgvsu.model;

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
        rotation = new Vector3f(0, 0, 0f);
        scale = new Vector3f(1f, 1f, 1f);
    }

    public ArrayList<Vector3f> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Vector3f> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<Vector2f> getTextureVertices() {
        return textureVertices;
    }

    public void setTextureVertices(ArrayList<Vector2f> textureVertices) {
        this.textureVertices = textureVertices;
    }

    public ArrayList<Vector3f> getNormals() {
        return normals;
    }

    public void setNormals(ArrayList<Vector3f> normals) {
        this.normals = normals;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public void setPolygons(ArrayList<Polygon> polygons) {
        this.polygons = polygons;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void addVertex(Vector3f vertex) {
        vertices.add(vertex);
    }

    public void addTextureVertex(Vector2f textureVertex) {
        textureVertices.add(textureVertex);
    }

    public void addNormal(Vector3f normal) {
        normals.add(normal);
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public Polygon getFirstPolygon() {
        return polygons.get(0);
    }


    public int getVerticesSize() {
        return vertices.size();
    }

    public int getTextureVerticesSize() {
        return textureVertices.size();
    }

    public int getNormalsSize() {
        return normals.size();
    }

    public int getPolygonsSize() {
        return polygons.size();
    }

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();
    public ArrayList<Group> groups = new ArrayList<>();

    public Matrix4x4 getModelMatrix() {
        return getTransformMatrix()
                .multiplyMM(getRotateMatrix())
                .multiplyMM(getScaleMatrix());
    }

    private Matrix4x4 getScaleMatrix() {
        return GraphicConveyor.scale(scale.x(), scale.y(), scale.z());
    }

    private Matrix4x4 getRotateMatrix() {
        return GraphicConveyor.rotate(rotation.z(), rotation.y(), rotation.x());
    }

    private Matrix4x4 getTransformMatrix() {
        return GraphicConveyor.transform(position.x(), position.y(), position.z());
    }
}
