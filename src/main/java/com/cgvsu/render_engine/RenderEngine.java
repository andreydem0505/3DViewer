package com.cgvsu.render_engine;

import java.util.Arrays;
import java.util.List;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Matrix4x4;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.rasterization.Bresenham;
import com.cgvsu.rasterization.PlainColorTrianglePainter;
import com.cgvsu.rasterization.PlainColorWithLightningTrianglePainter;
import com.cgvsu.rasterization.TriangleRasterization;
import javafx.scene.paint.Color;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final PixelWriter pixelWriter,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height)
    {
        Matrix4x4 modelMatrix = rotateScaleTranslate();
        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix();

        Matrix4x4 modelViewProjectionMatrix = new Matrix4x4(modelMatrix);
        modelViewProjectionMatrix = modelViewProjectionMatrix.multiplyMM(viewMatrix);
        modelViewProjectionMatrix = modelViewProjectionMatrix.multiplyMM(projectionMatrix);

        Vector3f[] resultPoints = new Vector3f[mesh.vertices.size()];
        Vector3f[] normals = new Vector3f[mesh.normals.size()];
        for (int i = 0; i < mesh.vertices.size(); i++) {
            Vector3f vertex = mesh.vertices.get(i);
            Vector3f normal = mesh.normals.get(i);
            normals[i] = new Vector3f(normal.x(), normal.y(), normal.z());
            Vector3f vertexVecmath = new Vector3f(vertex.x(), vertex.y(), vertex.z());
            Vector3f projectedVertex = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
            Vector2f resultPoint = vertexToPoint(projectedVertex, width, height);
            resultPoints[i] = new Vector3f(resultPoint.x(), resultPoint.y(), projectedVertex.z());
        }

        fillWithColorAndLightning(pixelWriter, resultPoints, mesh.polygons, normals, mesh.vertices, camera.getPosition(), Color.BLUE);
//        fillWithColor(pixelWriter, resultPoints, mesh.polygons, Color.CYAN);

        for (Polygon curPolygon : mesh.polygons) {
            List<Integer> polygonVertices = curPolygon.getVertexIndices();
            final int nVerticesInPolygon = polygonVertices.size();

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; vertexInPolygonInd++)
                drawLine(pixelWriter, resultPoints, polygonVertices, vertexInPolygonInd - 1, vertexInPolygonInd);

            if (nVerticesInPolygon > 0)
                drawLine(pixelWriter, resultPoints, polygonVertices, nVerticesInPolygon - 1, 0);
        }
    }

    private static void drawLine(PixelWriter pixelWriter, Vector3f[] resultPoints, List<Integer> vertices, int i, int j) {
        Bresenham.drawBresenhamLine(
                pixelWriter,
                Math.round(resultPoints[vertices.get(i)].x()),
                Math.round(resultPoints[vertices.get(i)].y()),
                resultPoints[vertices.get(i)].z(),
                Math.round(resultPoints[vertices.get(j)].x()),
                Math.round(resultPoints[vertices.get(j)].y()),
                resultPoints[vertices.get(j)].z()
        );
    }

    private static void fillWithColor(PixelWriter pixelWriter, Vector3f[] resultPoints, List<Polygon> polygons, Color color) {
        for (Polygon curPolygon : polygons) {
            for (int[] triangle : curPolygon.getTriangles()) {
                int[] x = Arrays.stream(triangle).map(e -> Math.round(resultPoints[e].x())).toArray();
                int[] y = Arrays.stream(triangle).map(e -> Math.round(resultPoints[e].y())).toArray();
                double[] z = Arrays.stream(triangle).mapToDouble(e -> resultPoints[e].z()).toArray();
                new TriangleRasterization(new PlainColorTrianglePainter(pixelWriter, x, y, z, color))
                        .fillTriangle();
            }
        }
    }

    private static void fillWithColorAndLightning(
            PixelWriter pixelWriter,
            Vector3f[] resultPoints,
            List<Polygon> polygons,
            Vector3f[] normals,
            List<Vector3f> vertices,
            Vector3f lightSource,
            Color color) {
        for (Polygon curPolygon : polygons) {
            for (int[] triangle : curPolygon.getTriangles()) {
                int[] x = Arrays.stream(triangle).map(e -> Math.round(resultPoints[e].x())).toArray();
                int[] y = Arrays.stream(triangle).map(e -> Math.round(resultPoints[e].y())).toArray();
                double[] z = Arrays.stream(triangle).mapToDouble(e -> resultPoints[e].z()).toArray();
                Vector3f[] n = new Vector3f[3];
                Vector3f[] v = new Vector3f[3];
                for (int i = 0; i < 3; i++) {
                    n[i] = normals[triangle[i]];
                    v[i] = vertices.get(triangle[i]);
                }
                new TriangleRasterization(new PlainColorWithLightningTrianglePainter(pixelWriter, x, y, z, n, v, lightSource, color))
                        .fillTriangle();
            }
        }
    }
}