package com.cgvsu.rasterization;

import java.awt.*;

public class InterpolationResult {
    public float[] barycentricCoordinates;
    public Color color;
    public final double z;

    public InterpolationResult(float[] barycentricCoordinates, Color color, double z) {
        this.barycentricCoordinates = barycentricCoordinates;
        this.color = color;
        this.z = z;
    }
}
