package com.cgvsu.math;

import com.cgvsu.model.Polygon;

import java.util.*;

public class Linal {
    private static final Vector3f zero = new Vector3f(0f, 0f, 0f);
    public static Vector3f subtract(Vector3f a, Vector3f b){
        return new Vector3f(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vector3f add(Vector3f a, Vector3f b){
        return new Vector3f(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector3f mean(List<Vector3f> vectors){
        Vector3f result = new Vector3f(0f, 0f, 0f);

        for (Vector3f vector : vectors){
            result.x += vector.x;
            result.y += vector.y;
            result.z += vector.z;
        }
        result.x /= vectors.size();
        result.y /= vectors.size();
        result.z /= vectors.size();

        return result;
    }

    public static Vector3f vectorProduct(Vector3f a, Vector3f b){
        float i = a.y * b.z - a.z * b.y;
        float j = a.z * b.x - a.x * b.z;
        float k = a.x * b.y - a.y * b.x;

        return new Vector3f(i, j, k);
    }

    public static Vector3f normalize(Vector3f a){
        if(a.equals(zero))
            return zero;

        float len = (float) Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
        return new Vector3f(a.x / len, a.y / len, a.z / len);
    }

    private static void putToDict(Map<Integer, Pair> dict, ArrayList<Vector3f> vertices, ArrayList<Integer> indices, int prevIndex, int currIndex, int nextIndex){
        Vector3f prevVertex = vertices.get(indices.get(prevIndex));
        Vector3f currVertex = vertices.get(indices.get(currIndex));
        Vector3f nextVertex = vertices.get(indices.get(nextIndex));

        Vector3f a = Linal.subtract(prevVertex, currVertex);
        Vector3f b = Linal.subtract(nextVertex, currVertex);
        Vector3f normal = Linal.normalize(Linal.vectorProduct(b, a));

        if (!dict.containsKey(indices.get(currIndex))) {
            Pair pair = new Pair();
            dict.put(indices.get(currIndex), pair);
        }
        dict.get(indices.get(currIndex)).v = Linal.add(dict.get(indices.get(currIndex)).v, normal);
        dict.get(indices.get(currIndex)).c++;
    }

    public static ArrayList<Vector3f> calculateVerticesNormals(ArrayList<Vector3f> vertices, ArrayList<Polygon> polygons){
        ArrayList<Vector3f> normals = new ArrayList<>();
//        идея - для каждой вершины ищем соседние вершины, считаем нормаль и прибавляем
//        делим на количество нормалей
        Map<Integer, Pair> dict = new HashMap<>();
        for (Polygon polygon : polygons) {
            ArrayList<Integer> indices = polygon.getVertexIndices();
            int prevIndex = indices.size() - 1;
            int currIndex = 0;
            int nextIndex = 1;
            while (nextIndex < indices.size()) {
                putToDict(dict, vertices, indices, prevIndex, currIndex, nextIndex);

                prevIndex = currIndex;
                currIndex = nextIndex;
                nextIndex++;
            }
            putToDict(dict, vertices, indices, prevIndex, currIndex, 0);
        }

        for (int i = 0; i < vertices.size(); i++){
            Pair pair = dict.get(i);
            Vector3f normal = pair.v;
            int amount = pair.c;

            normal.x /= amount;
            normal.y /= amount;
            normal.z /= amount;

            normal = Linal.normalize(normal);
            normals.add(normal);
        }

        return normals;
    }

    public static ArrayList<Vector3f> calculatePolygonNormals(ArrayList<Vector3f> vertices, ArrayList<Polygon> polygons){
        ArrayList<Vector3f> normals = new ArrayList<>();

        for (Polygon polygon : polygons){
            ArrayList<Integer> indices = polygon.getVertexIndices();

            Vector3f a = Linal.subtract(vertices.get(indices.get(0)), vertices.get(indices.get(1)));
            Vector3f b = Linal.subtract(vertices.get(indices.get(2)), vertices.get(indices.get(1)));
            Vector3f normal = Linal.normalize(Linal.vectorProduct(b, a));

            normals.add(normal);
        }

        return normals;
    }

    private static class Pair {
        Vector3f v = new Vector3f(0f, 0f, 0f);
        int c = 0;
    }

    private class NormalCalculationError extends RuntimeException {
        public NormalCalculationError(String message, int polygonIndex){
            super("Error calculating normal of polygon: " + polygonIndex + ". " + message);
        }
    }
}