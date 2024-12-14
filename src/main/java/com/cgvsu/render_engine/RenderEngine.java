package com.cgvsu.render_engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Matrix4x4;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.nmath.Vector4f;
import com.cgvsu.rasterization.*;


import javax.imageio.ImageIO;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {
    private Model mesh;
    private Vector3f[] resultPoints;
    private PixelWriter pixelWriter;
    private Vector3f[] normals;

    public void render(
            final PixelWriter pixelWriter,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final RenderMode renderMode) throws IOException {
        Matrix4x4 modelMatrix = mesh.getModelMatrix();
        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix();

        Matrix4x4 modelViewProjectionMatrix = new Matrix4x4(projectionMatrix);
        modelViewProjectionMatrix = modelViewProjectionMatrix.multiplyMM(viewMatrix);
        modelViewProjectionMatrix = modelViewProjectionMatrix.multiplyMM(modelMatrix);

        this.pixelWriter = pixelWriter;
        this.mesh = mesh;
        resultPoints = new Vector3f[mesh.vertices.size()];
        normals = new Vector3f[mesh.normals.size()];
        for (int i = 0; i < mesh.vertices.size(); i++) {
            Vector3f vertex = mesh.vertices.get(i);
            Vector3f normal = mesh.normals.get(i);

            // project vertices
            Vector4f vertex4 = new Vector4f(vertex.x(), vertex.y(), vertex.z(), 1.0f);
            vertex4 = modelViewProjectionMatrix.multiplyMV(vertex4);
            Vector3f projectedVertex = new Vector3f(
                    vertex4.x() / vertex4.w(),
                    vertex4.y() / vertex4.w(),
                    vertex4.z() / vertex4.w()
            );
            Vector2f resultPoint = vertexToPoint(projectedVertex, width, height);
            resultPoints[i] = new Vector3f(resultPoint.x(), resultPoint.y(), projectedVertex.z());

            //project normals
            Vector4f normal4 = new Vector4f(normal.x(), normal.y(), normal.z(), 1.0f);
            normal4 = modelMatrix.multiplyMV(normal4);
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
                fillWithTextureAndLightning(camera.getPosition(), ImageIO.read(renderMode.texture));
            } else {
                fillWithTexture(ImageIO.read(renderMode.texture));
            }
        }

        if (renderMode.grid)
            for (Polygon curPolygon : mesh.polygons) {
                List<Integer> polygonVertices = curPolygon.getVertexIndices();
                final int nVerticesInPolygon = polygonVertices.size();

                for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; vertexInPolygonInd++)
                    drawLine(pixelWriter, resultPoints, polygonVertices, vertexInPolygonInd - 1, vertexInPolygonInd);

                if (nVerticesInPolygon > 0)
                    drawLine(pixelWriter, resultPoints, polygonVertices, nVerticesInPolygon - 1, 0);
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
        int[] x, y;
        double[] z;
        for (Polygon curPolygon : mesh.polygons) {
            for (int[] triangle : curPolygon.getVertexIndicesTriangles()) {
                x = new int[3];
                y = new int[3];
                z = new double[3];
                for (int i = 0; i < 3; i++) {
                    x[i] = Math.round(resultPoints[triangle[i]].x());
                    y[i] = Math.round(resultPoints[triangle[i]].y());
                    z[i] = resultPoints[triangle[i]].z();
                }
                new TriangleRasterization(new PlainColorTrianglePainter(pixelWriter, x, y, z, color))
                        .fillTriangle();
            }
        }
    }

    private void fillWithColorAndLightning(Vector3f lightSource, Color color) {
        int[] x, y;
        double[] z;
        Vector3f[] n, v;
        for (Polygon curPolygon : mesh.polygons) {
            for (int[] triangle : curPolygon.getVertexIndicesTriangles()) {
                x = new int[3];
                y = new int[3];
                z = new double[3];
                n = new Vector3f[3];
                v = new Vector3f[3];
                for (int i = 0; i < 3; i++) {
                    x[i] = Math.round(resultPoints[triangle[i]].x());
                    y[i] = Math.round(resultPoints[triangle[i]].y());
                    z[i] = resultPoints[triangle[i]].z();
                    n[i] = normals[triangle[i]];
                    v[i] = mesh.vertices.get(triangle[i]);
                }
                new TriangleRasterization(new PlainColorWithLightningTrianglePainter(
                        pixelWriter, x, y, z, n, v, lightSource, color
                )).fillTriangle();
            }
        }
    }

    private void fillWithTexture(BufferedImage image) {
        int nTriangles;
        int[] x, y, vertexTriangle, textureTriangle;
        double[] z;
        Vector2f[] textureV;
        for (Polygon curPolygon : mesh.polygons) {
            nTriangles = curPolygon.getVertexIndicesTriangles().size();
            for (int i = 0; i < nTriangles; i++) {
                vertexTriangle = curPolygon.getVertexIndicesTriangles().get(i);
                textureTriangle = curPolygon.getTextureVertexIndicesTriangles().get(i);
                x = new int[3];
                y = new int[3];
                z = new double[3];
                textureV = new Vector2f[3];
                for (int j = 0; j < 3; j++) {
                    x[j] = Math.round(resultPoints[vertexTriangle[j]].x());
                    y[j] = Math.round(resultPoints[vertexTriangle[j]].y());
                    z[j] = resultPoints[vertexTriangle[j]].z();
                    textureV[j] = mesh.textureVertices.get(textureTriangle[j]);
                }
                new TriangleRasterization(new TextureTrianglePainter(pixelWriter, x, y, z, textureV, image))
                        .fillTriangle();
            }
        }
    }

    private void fillWithTextureAndLightning(Vector3f lightSource, BufferedImage image) {
        int nTriangles;
        int[] x, y, vertexTriangle, textureTriangle;
        double[] z;
        Vector2f[] textureV;
        Vector3f[] n, v;
        for (Polygon curPolygon : mesh.polygons) {
            nTriangles = curPolygon.getVertexIndicesTriangles().size();
            for (int i = 0; i < nTriangles; i++) {
                vertexTriangle = curPolygon.getVertexIndicesTriangles().get(i);
                textureTriangle = curPolygon.getTextureVertexIndicesTriangles().get(i);
                x = new int[3];
                y = new int[3];
                z = new double[3];
                textureV = new Vector2f[3];
                n = new Vector3f[3];
                v = new Vector3f[3];
                for (int j = 0; j < 3; j++) {
                    x[j] = Math.round(resultPoints[vertexTriangle[j]].x());
                    y[j] = Math.round(resultPoints[vertexTriangle[j]].y());
                    z[j] = resultPoints[vertexTriangle[j]].z();
                    textureV[j] = mesh.textureVertices.get(textureTriangle[j]);
                    n[j] = normals[vertexTriangle[j]];
                    v[j] = mesh.vertices.get(vertexTriangle[j]);
                }
                new TriangleRasterization(new TextureWithLightningTrianglePainter(
                        pixelWriter, x, y, z, textureV, image, n, v, lightSource)
                ).fillTriangle();
            }
        }
    }
}