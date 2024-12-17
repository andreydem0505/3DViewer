package com.cgvsu.model;

import com.cgvsu.io.objreader.exceptions.FaceWordException;

import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private ArrayList<Integer> vertexIndices;
    private ArrayList<Integer> textureVertexIndices;
    private ArrayList<Integer> normalIndices;
    private List<int[]> vertexIndicesTriangles;
    private List<int[]> textureVertexIndicesTriangles;


    public Polygon() {
        vertexIndices = new ArrayList<Integer>();
        textureVertexIndices = new ArrayList<Integer>();
        normalIndices = new ArrayList<Integer>();
    }

    public void setVertexIndices(ArrayList<Integer> vertexIndices) {
        assert vertexIndices.size() >= 3;
        this.vertexIndices = vertexIndices;
    }

    public void setTextureVertexIndices(ArrayList<Integer> textureVertexIndices) {
        assert textureVertexIndices.size() >= 3;
        this.textureVertexIndices = textureVertexIndices;
    }

    public void setNormalIndices(ArrayList<Integer> normalIndices) {
        assert normalIndices.size() >= 3;
        this.normalIndices = normalIndices;
    }

    public ArrayList<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public ArrayList<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public ArrayList<Integer> getNormalIndices() {
        return normalIndices;
    }

    public List<int[]> getVertexIndicesTriangles() {
        return vertexIndicesTriangles;
    }

    public void setVertexIndicesTriangles(List<int[]> triangles) {
        this.vertexIndicesTriangles = triangles;
    }

    public List<int[]> getTextureVertexIndicesTriangles() {
        return textureVertexIndicesTriangles;
    }

    public void setTextureVertexIndicesTriangles(List<int[]> textureVertexIndicesTriangles) {
        this.textureVertexIndicesTriangles = textureVertexIndicesTriangles;
    }

    private int lineIndex;
    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }
    public boolean hasTexture() {
        return !textureVertexIndices.isEmpty();
    }

    public void checkIndices(int verticesSize, int textureVerticesSize, int normalsSize) {
        for (int i = 0; i < vertexIndices.size(); i++) {
            int vertexIndex = vertexIndices.get(i);
            if (vertexIndex >= verticesSize || vertexIndex < 0) {
                throw new FaceWordException("vertex", lineIndex, i + 1);
            }
        }

        for (int i = 0; i < textureVertexIndices.size(); i++) {
            int textureVertexIndex = textureVertexIndices.get(i);
            if (textureVertexIndex >= textureVerticesSize || textureVertexIndex < 0) {
                throw new FaceWordException("texture vertex", lineIndex, i + 1);
            }
        }

        for (int i = 0; i < normalIndices.size(); i++) {
            int normalIndex = normalIndices.get(i);
            if (normalIndex >= normalsSize || normalIndex < 0) {
                throw new FaceWordException("normal", lineIndex, i + 1);
            }
        }
    }
}
