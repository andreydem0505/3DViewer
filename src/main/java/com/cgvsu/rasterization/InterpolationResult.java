package com.cgvsu.rasterization;

import javafx.scene.paint.Color;

public class InterpolationResult {
    public float[] barycentricCoordinates;
    public final Color color;
    public final double z;

    public InterpolationResult(float[] barycentricCoordinates, Color color, double z) {
        this.barycentricCoordinates = barycentricCoordinates;
        this.color = color;
        this.z = z;
    }
}
