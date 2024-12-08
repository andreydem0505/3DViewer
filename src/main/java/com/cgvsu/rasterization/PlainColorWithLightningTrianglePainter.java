package com.cgvsu.rasterization;

import com.cgvsu.math.Linal;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.PixelWriter;
import javafx.scene.paint.Color;

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
        float[] barycentricCoordinates = result.barycentricCoordinates;
        boolean good = false;
        for (float coord : barycentricCoordinates){
            if (Math.abs(coord) > Linal.eps) {
                good = true;
                break;
            }
        }
        if (!good)
            return new InterpolationResult(
                    barycentricCoordinates,
                    new Color(color.getRed(), color.getGreen(), color.getBlue(), 1),
                    Float.MAX_VALUE
            );


        Vector3f n = new Vector3f(
                -barycentricCoordinates[0] * normals[0].x() +
                        -barycentricCoordinates[1] * normals[1].x() +
                        -barycentricCoordinates[2] * normals[2].x(),
                -barycentricCoordinates[0] * normals[0].y() +
                        -barycentricCoordinates[1] * normals[1].y() +
                        -barycentricCoordinates[2] * normals[2].y(),
                -barycentricCoordinates[0] * normals[0].z() +
                        -barycentricCoordinates[1] * normals[1].z() +
                        -barycentricCoordinates[2] * normals[2].z()
        );
        n.normalize();
        Vector3f ray = GraphicConveyor.getRay(lightSource, new Vector3f(
                barycentricCoordinates[0] * vertices[0].x() +
                        barycentricCoordinates[1] * vertices[1].x() +
                        barycentricCoordinates[2] * vertices[2].x(),
                barycentricCoordinates[0] * vertices[0].y() +
                        barycentricCoordinates[1] * vertices[1].y() +
                        barycentricCoordinates[2] * vertices[2].y(),
                barycentricCoordinates[0] * vertices[0].z() +
                        barycentricCoordinates[1] * vertices[1].z() +
                        barycentricCoordinates[2] * vertices[2].z()
        ));
        ray.normalize();
        float l = Math.abs(n.dotProduct(ray));
        return new InterpolationResult(
                barycentricCoordinates,
                new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, k * (1 - l))),
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
