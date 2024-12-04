package com.cgvsu.triangulation;


import java.util.ArrayList;
import java.util.List;


public final class Triangulation {
    private Triangulation() {
        throw new UnsupportedOperationException("Cannot be instantiated.");
    }

    private static void checkVertexIndicesCount(int n) {
        if (n < 3) {
            throw new IllegalArgumentException("Not enough vertex indices for a polygon");
        }
    }

    public static List<int[]> convexPolygonTriangulate(List<Integer> vertexIndices) {
        int vertexIndicesCount = vertexIndices.size();
        checkVertexIndicesCount(vertexIndicesCount);

        List<int[]> triangles = new ArrayList<>(vertexIndicesCount - 2);

        for (int i = 2; i < vertexIndicesCount; i++) {
            triangles.add(new int[]{vertexIndices.get(0), vertexIndices.get(i), vertexIndices.get(i - 1)});
        }

        return triangles;
    }
}
