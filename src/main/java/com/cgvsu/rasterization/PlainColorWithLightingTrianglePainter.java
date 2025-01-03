package com.cgvsu.rasterization;

import com.cgvsu.nmath.Vector3f;
import com.cgvsu.render_engine.PixelWriter;

import java.awt.*;

public class PlainColorWithLightingTrianglePainter extends PlainColorTrianglePainter {
    private final Vector3f[] normals;
    private final Vector3f[] vertices;
    private final Vector3f lightSource;

    public PlainColorWithLightingTrianglePainter(
            PixelWriter pixelWriter,
            int[] arrX,
            int[] arrY,
            double[] arrZ,
            Vector3f[] normals,
            Vector3f[] vertices,
            Vector3f lightSource,
            Color color) {
        super(pixelWriter, arrX, arrY, arrZ, color);
        this.normals = normals;
        this.vertices = vertices;
        this.lightSource = lightSource;
    }

    @Override
    protected InterpolationResult interpolate(int x, int y) {
        InterpolationResult result = super.interpolate(x, y);
        return Lighting.light(result, normals, vertices, lightSource);
    }

    @Override
    protected void swap(int i, int j) {
        super.swap(i, j);
        Vector3f tempNormal = normals[i];
        Vector3f tempVertex = vertices[i];
        normals[i] = normals[j];
        vertices[i] = vertices[j];
        normals[j] = tempNormal;
        vertices[j] = tempVertex;
    }
}
