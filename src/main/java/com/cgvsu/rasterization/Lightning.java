package com.cgvsu.rasterization;

import com.cgvsu.math.Barycentric;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;

import java.awt.*;

public class Lightning {
    private final static float k = 0.95f;

    public static InterpolationResult light(
            InterpolationResult interpolation, Vector3f[] normals,
            Vector3f[] vertices, Vector3f lightSource
    ) {
        if (interpolation == null) return null;
        float[] barycentricCoordinates = interpolation.barycentricCoordinates;

        Vector3f n = Barycentric.getVector(barycentricCoordinates, normals);
        n.scale(-1);
        n.normalize();
        Vector3f ray = GraphicConveyor.getRay(lightSource, Barycentric.getVector(barycentricCoordinates, vertices));
        ray.normalize();
        float l = Math.abs(n.dotProduct(ray));
        Color color = interpolation.color;
        return new InterpolationResult(
                barycentricCoordinates,
                new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (Math.max(0, k * (1 - l)) * 255)),
                interpolation.z
        );
    }
}
