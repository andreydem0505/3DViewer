package com.cgvsu.render_engine;

import java.util.Arrays;
import java.util.List;

import com.cgvsu.math.Vector3f;
import javax.vecmath.*;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.rasterization.Bresenham;
import com.cgvsu.rasterization.PlainColorTrianglePainter;
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
        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        Point3f[] resultPoints = new Point3f[mesh.vertices.size()];
        for (int i = 0; i < mesh.vertices.size(); i++) {
            Vector3f vertex = mesh.vertices.get(i);
            javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z);
            javax.vecmath.Vector3f projectedVertex = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
            Point2f resultPoint = vertexToPoint(projectedVertex, width, height);
            resultPoints[i] = new Point3f(resultPoint.x, resultPoint.y, projectedVertex.z);
        }

        fillWithColor(pixelWriter, resultPoints, mesh.polygons, Color.CYAN);

        for (Polygon curPolygon : mesh.polygons) {
            final int nVerticesInPolygon = curPolygon.getVertexIndices().size();
            List<Integer> polygonVertices = curPolygon.getVertexIndices();

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; vertexInPolygonInd++) {
                Bresenham.drawBresenhamLine(
                        pixelWriter,
                        Math.round(resultPoints[polygonVertices.get(vertexInPolygonInd - 1)].x),
                        Math.round(resultPoints[polygonVertices.get(vertexInPolygonInd - 1)].y),
                        resultPoints[polygonVertices.get(vertexInPolygonInd - 1)].z,
                        Math.round(resultPoints[polygonVertices.get(vertexInPolygonInd)].x),
                        Math.round(resultPoints[polygonVertices.get(vertexInPolygonInd)].y),
                        resultPoints[polygonVertices.get(vertexInPolygonInd)].z
                );
            }

            if (nVerticesInPolygon > 0)
                Bresenham.drawBresenhamLine(
                        pixelWriter,
                        Math.round(resultPoints[polygonVertices.get(nVerticesInPolygon - 1)].x),
                        Math.round(resultPoints[polygonVertices.get(nVerticesInPolygon - 1)].y),
                        resultPoints[polygonVertices.get(nVerticesInPolygon - 1)].z,
                        Math.round(resultPoints[polygonVertices.get(0)].x),
                        Math.round(resultPoints[polygonVertices.get(0)].y),
                        resultPoints[polygonVertices.get(0)].z
                );
        }
    }

    private static void fillWithColor(PixelWriter pixelWriter, Point3f[] resultPoints, List<Polygon> polygons, Color color) {
        for (Polygon curPolygon : polygons) {
            for (int[] triangle : curPolygon.getTriangles()) {
                int[] x = Arrays.stream(triangle).map(e -> Math.round(resultPoints[e].x)).toArray();
                int[] y = Arrays.stream(triangle).map(e -> Math.round(resultPoints[e].y)).toArray();
                double[] z = Arrays.stream(triangle).mapToDouble(e -> resultPoints[e].z).toArray();
                new TriangleRasterization(new PlainColorTrianglePainter(pixelWriter, x, y, z, color))
                        .fillTriangle();
            }
        }
    }
}