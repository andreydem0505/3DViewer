package com.cgvsu.triangulation;


import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;


public final class Triangulation {
    public static void convexPolygonTriangulate(Polygon polygon) {
        List<Integer> vertexIndices = polygon.getVertexIndices();
        int vertexIndicesCount = vertexIndices.size();

        List<int[]> triangles = new ArrayList<>(vertexIndicesCount - 2);
        for (int i = 2; i < vertexIndicesCount; i++) {
            triangles.add(new int[]{vertexIndices.get(0), vertexIndices.get(i), vertexIndices.get(i - 1)});
        }
        polygon.setVertexIndicesTriangles(triangles);

        if (!polygon.getTextureVertexIndices().isEmpty()) {
            List<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
            triangles = new ArrayList<>(vertexIndicesCount - 2);
            for (int i = 2; i < vertexIndicesCount; i++) {
                triangles.add(new int[]{textureVertexIndices.get(0), textureVertexIndices.get(i), textureVertexIndices.get(i - 1)});
            }
            polygon.setTextureVertexIndicesTriangles(triangles);
        }
    }
}
