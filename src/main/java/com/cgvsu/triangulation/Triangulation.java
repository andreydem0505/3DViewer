package com.cgvsu.triangulation;


import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;


public final class Triangulation {
    public static void convexPolygonTriangulate(Polygon polygon) {
        polygon.setVertexIndicesTriangles(triangulate(polygon.getVertexIndices()));
        if (!polygon.getTextureVertexIndices().isEmpty())
            polygon.setTextureVertexIndicesTriangles(triangulate(polygon.getTextureVertexIndices()));
    }

    private static List<int[]> triangulate(List<Integer> vertexIndices) {
        List<int[]> triangles = new ArrayList<>(vertexIndices.size() - 2);
        for (int i = 2; i < vertexIndices.size(); i++) {
            triangles.add(new int[]{vertexIndices.get(0), vertexIndices.get(i), vertexIndices.get(i - 1)});
        }
        return triangles;
    }
}
