package com.cgvsu.render_engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Matrix4x4;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.nmath.Vector4f;
import com.cgvsu.rasterization.*;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {
    private Model mesh;
    private Vector3f[] resultPoints;
    private final PixelWriter pixelWriter;
    private Vector3f[] normals;

    public RenderEngine(final PixelWriter pixelWriter) {
        this.pixelWriter = pixelWriter;
    }

    public void render(
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final RenderMode renderMode) {
        Matrix4x4 modelMatrix = mesh.getModelMatrix();
        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix();

        Matrix4x4 modelViewProjectionMatrix = new Matrix4x4(projectionMatrix);
        modelViewProjectionMatrix = modelViewProjectionMatrix.multiplyMM(viewMatrix);
        modelViewProjectionMatrix = modelViewProjectionMatrix.multiplyMM(modelMatrix);

        this.mesh = mesh;
        resultPoints = new Vector3f[mesh.vertices.size()];
        normals = new Vector3f[mesh.normals.size()];
        Vector4f vertex4, normal4;
        Vector3f vertex, normal, projectedVertex;
        Vector2f resultPoint;
        for (int i = 0; i < mesh.vertices.size(); i++) {
            vertex = mesh.vertices.get(i);
            normal = mesh.normals.get(i);

            // project vertices
            vertex4 = modelViewProjectionMatrix.multiplyMV(
                    new Vector4f(vertex.x(), vertex.y(), vertex.z(), 1.0f)
            );
            projectedVertex = new Vector3f(
                    vertex4.x() / vertex4.w(),
                    vertex4.y() / vertex4.w(),
                    vertex4.z() / vertex4.w()
            );
            resultPoint = vertexToPoint(projectedVertex, width, height);
            resultPoints[i] = new Vector3f(resultPoint.x(), resultPoint.y(), projectedVertex.z());

            //project normals
            normal4 = modelMatrix.multiplyMV(new Vector4f(normal.x(), normal.y(), normal.z(), 1.0f));
            normals[i] = new Vector3f(normal4.x(), normal4.y(), normal4.z());
        }

        if (renderMode.color != null) {
            if (renderMode.light) {
                fillWithColorAndLightning(camera.getPosition(), renderMode.color);
            } else {
                fillWithColor(renderMode.color);
            }
        } else if (renderMode.texture != null) {
            if (renderMode.light) {
                fillWithTextureAndLightning(camera.getPosition(), renderMode.texture);
            } else {
                fillWithTexture(renderMode.texture);
            }
        }

        if (renderMode.grid) {
            int nVerticesInPolygon;
            List<Integer> polygonVertices;
            for (Polygon curPolygon : mesh.polygons) {
                polygonVertices = curPolygon.getVertexIndices();
                nVerticesInPolygon = polygonVertices.size();

                for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; vertexInPolygonInd++)
                    drawLine(pixelWriter, resultPoints, polygonVertices, vertexInPolygonInd - 1, vertexInPolygonInd);

                if (nVerticesInPolygon > 0)
                    drawLine(pixelWriter, resultPoints, polygonVertices, nVerticesInPolygon - 1, 0);
            }
        }

        pixelWriter.draw();
    }

    private void drawLine(PixelWriter pixelWriter, Vector3f[] resultPoints, List<Integer> vertices, int i, int j) {
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

    private void fillWithColor(Color color) {
        int[] x = new int[3];
        int[] y = new int[3];
        double[] z = new double[3];
        for (Polygon curPolygon : mesh.polygons) {
            for (int[] triangle : curPolygon.getVertexIndicesTriangles()) {
                for (int i = 0; i < 3; i++) {
                    x[i] = Math.round(resultPoints[triangle[i]].x());
                    y[i] = Math.round(resultPoints[triangle[i]].y());
                    z[i] = resultPoints[triangle[i]].z();
                }
                TriangleRasterization.fillTriangle(new PlainColorTrianglePainter(pixelWriter, x, y, z, color));
            }
        }
    }

    private void fillWithColorAndLightning(Vector3f lightSource, Color color) {
        int[] x = new int[3];
        int[] y = new int[3];
        double[] z = new double[3];
        Vector3f[] n = new Vector3f[3];
        Vector3f[] v = new Vector3f[3];
        for (Polygon curPolygon : mesh.polygons) {
            for (int[] triangle : curPolygon.getVertexIndicesTriangles()) {
                for (int i = 0; i < 3; i++) {
                    x[i] = Math.round(resultPoints[triangle[i]].x());
                    y[i] = Math.round(resultPoints[triangle[i]].y());
                    z[i] = resultPoints[triangle[i]].z();
                    n[i] = normals[triangle[i]];
                    v[i] = mesh.vertices.get(triangle[i]);
                }
                TriangleRasterization.fillTriangle(new PlainColorWithLightningTrianglePainter(
                        pixelWriter, x, y, z, n, v, lightSource, color
                ));
            }
        }
    }

    private void fillWithTexture(BufferedImage image) {
        int nTriangles;
        int[] x = new int[3];
        int[] y = new int[3];
        double[] z = new double[3];
        Vector2f[] textureV = new Vector2f[3];
        for (Polygon curPolygon : mesh.polygons) {
            nTriangles = curPolygon.getVertexIndicesTriangles().size();
            for (int i = 0; i < nTriangles; i++) {
                int[] vertexTriangle = curPolygon.getVertexIndicesTriangles().get(i);
                int[] textureTriangle = curPolygon.getTextureVertexIndicesTriangles().get(i);
                for (int j = 0; j < 3; j++) {
                    x[j] = Math.round(resultPoints[vertexTriangle[j]].x());
                    y[j] = Math.round(resultPoints[vertexTriangle[j]].y());
                    z[j] = resultPoints[vertexTriangle[j]].z();
                    textureV[j] = mesh.textureVertices.get(textureTriangle[j]);
                }
                TriangleRasterization.fillTriangle(new TextureTrianglePainter(pixelWriter, x, y, z, textureV, image));
            }
        }
    }

    private void fillWithTextureAndLightning(Vector3f lightSource, BufferedImage image) {
        int nTriangles;
        int[] x = new int[3];
        int[] y = new int[3];
        double[] z = new double[3];
        Vector2f[] textureV = new Vector2f[3];
        Vector3f[] n = new Vector3f[3];
        Vector3f[] v = new Vector3f[3];
        for (Polygon curPolygon : mesh.polygons) {
            nTriangles = curPolygon.getVertexIndicesTriangles().size();
            for (int i = 0; i < nTriangles; i++) {
                int[] vertexTriangle = curPolygon.getVertexIndicesTriangles().get(i);
                int[] textureTriangle = curPolygon.getTextureVertexIndicesTriangles().get(i);
                for (int j = 0; j < 3; j++) {
                    x[j] = Math.round(resultPoints[vertexTriangle[j]].x());
                    y[j] = Math.round(resultPoints[vertexTriangle[j]].y());
                    z[j] = resultPoints[vertexTriangle[j]].z();
                    textureV[j] = mesh.textureVertices.get(textureTriangle[j]);
                    n[j] = normals[vertexTriangle[j]];
                    v[j] = mesh.vertices.get(vertexTriangle[j]);
                }
                TriangleRasterization.fillTriangle(new TextureWithLightningTrianglePainter(
                        pixelWriter, x, y, z, textureV, image, n, v, lightSource
                ));
            }
        }
    }
}