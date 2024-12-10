package com.cgvsu.math;

import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Matrix3x3;
import com.cgvsu.nmath.Vector3f;

import java.util.*;

public class Linal {
    public static final float pi = (float) Math.PI;
    public static final float pi2 = (float) Math.PI * 2;
    public static final float pi_half = (float) Math.PI / 2;
    public static final float eps = 1e-7f;
    public static final Vector3f zero = new Vector3f(0f, 0f, 0f);

    public static Vector3f add(Vector3f a, Vector3f b) {
        Vector3f c = new Vector3f(a);
        c.add(b);
        return c;
    }

    public static Vector3f subtract(Vector3f a, Vector3f b) {
        Vector3f c = new Vector3f(a);
        c.subtract(b);
        return c;
    }

    public static Vector3f crossProduct(Vector3f a, Vector3f b) {
        Vector3f c = new Vector3f(a);
        c = c.crossProduct(b);

        return c;
    }

    public static Vector3f normalize(Vector3f a) {
        Vector3f b = new Vector3f(a);
        b.normalize();

        return b;
    }

    public static Vector3f mean(List<Vector3f> vectors) {
        Vector3f result = new Vector3f(0f, 0f, 0f);

        for (Vector3f vector : vectors) {
            result.add(vector);
        }
        result.divide(vectors.size());

        return result;
    }

    public static float getDeterminant3x3(float a, float b, float c,
                                          float d, float e, float f,
                                          float g, float h, float i) {
        return (a * e * i) + (d * h * c) + (b * f * g) - (g * e * c) - (a * f * h) - (d * b * i);
    }

    public static float[] solveKramer3x3(Matrix3x3 coefs, Vector3f adjoint) {
        float determinant = coefs.determinant();
        if (Math.abs(determinant) < eps)
            return new float[]{0f, 0f, 0f};

        float determinant1 = getDeterminant3x3(
                adjoint.x(), coefs.get(0, 1), coefs.get(0, 2),
                adjoint.y(), coefs.get(1, 1), coefs.get(1, 2),
                adjoint.z(), coefs.get(2, 1), coefs.get(2, 2));
        float determinant2 = getDeterminant3x3(
                coefs.get(0, 0), adjoint.x(), coefs.get(0, 2),
                coefs.get(1, 0), adjoint.y(), coefs.get(1, 2),
                coefs.get(2, 0), adjoint.z(), coefs.get(2, 2));
        float determinant3 = getDeterminant3x3(
                coefs.get(0, 0), coefs.get(0, 1), adjoint.x(),
                coefs.get(1, 0), coefs.get(1, 1), adjoint.y(),
                coefs.get(2, 0), coefs.get(2, 1), adjoint.z());

        return new float[]{determinant1 / determinant, determinant2 / determinant, determinant3 / determinant};
    }

    private static void putToDict(Map<Integer, Pair> dict, ArrayList<Vector3f> vertices, ArrayList<Integer> indices, int prevIndex, int currIndex, int nextIndex) {
        Vector3f prevVertex = vertices.get(indices.get(prevIndex));
        Vector3f currVertex = vertices.get(indices.get(currIndex));
        Vector3f nextVertex = vertices.get(indices.get(nextIndex));

        Vector3f a = subtract(prevVertex, currVertex);
        Vector3f b = subtract(nextVertex, currVertex);
        Vector3f normal = b.crossProduct(a).normalize();

        if (!dict.containsKey(indices.get(currIndex))) {
            Pair pair = new Pair();
            dict.put(indices.get(currIndex), pair);
        }
        dict.get(indices.get(currIndex)).v.add(normal);
        dict.get(indices.get(currIndex)).c++;
    }

    public static ArrayList<Vector3f> calculateVerticesNormals(ArrayList<Vector3f> vertices, ArrayList<Polygon> polygons) {
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

        for (int i = 0; i < vertices.size(); i++) {
            if (dict.get(i) == null) {
                normals.add(Linal.zero);
                continue;
            }

            Pair pair = dict.get(i);
            Vector3f normal = pair.v;
            int amount = pair.c;

            normal.divide(amount);

            normal = normal.normalize();
            normals.add(normal);
        }

        return normals;
    }

    public static ArrayList<Vector3f> calculatePolygonNormals(ArrayList<Vector3f> vertices, ArrayList<Polygon> polygons) {
        ArrayList<Vector3f> normals = new ArrayList<>();

        for (Polygon polygon : polygons) {
            ArrayList<Integer> indices = polygon.getVertexIndices();

            Vector3f a = subtract(vertices.get(indices.get(0)), vertices.get(indices.get(1)));
            Vector3f b = subtract(vertices.get(indices.get(2)), vertices.get(indices.get(1)));
            Vector3f normal = b.crossProduct(a).normalize();

            normals.add(normal);
        }

        return normals;
    }

    private static class Pair {
        Vector3f v = new Vector3f(0f, 0f, 0f);
        int c = 0;
    }
}