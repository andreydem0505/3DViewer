package com.cgvsu.rasterization;

import com.cgvsu.math.Barycentric;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.PixelWriter;

import java.awt.*;

public class PlainColorWithLightningTrianglePainter extends PlainColorTrianglePainter {
    private final Vector3f[] normals;
    private final Vector3f[] vertices;
    private final Vector3f lightSource;
    private final float k = 0.9f;

    public PlainColorWithLightningTrianglePainter(
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
        if (result == null) return null;

        float[] barycentricCoordinates = result.barycentricCoordinates;

        Vector3f n = Barycentric.getVector(barycentricCoordinates, normals);
        n.scale(-1);
        n.normalize();
        Vector3f ray = GraphicConveyor.getRay(lightSource, Barycentric.getVector(barycentricCoordinates, vertices));
        ray.normalize();
        float l = Math.abs(n.dotProduct(ray));
        return new InterpolationResult(
                barycentricCoordinates,
                new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (Math.max(0, k * (1 - l)) * 255)),
                result.z
        );
    }

    @Override
    protected void swap(int i, int j) {
        super.swap(i, j);
        Vector3f tempNormal = normals[i];
        Vector3f tempVertix = vertices[i];
        normals[i] = normals[j];
        vertices[i] = vertices[j];
        normals[j] = tempNormal;
        vertices[j] = tempVertix;
    }
}
