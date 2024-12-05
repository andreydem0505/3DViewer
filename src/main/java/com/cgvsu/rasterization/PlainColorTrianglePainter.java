package com.cgvsu.rasterization;

import com.cgvsu.math.Barycentric;
import com.cgvsu.render_engine.PixelWriter;
import javafx.scene.paint.Color;

public class PlainColorTrianglePainter extends TrianglePainter {
    private final double[] arrZ;
    protected final Color color;

    public PlainColorTrianglePainter(PixelWriter pixelWriter, int[] arrX, int[] arrY, double[] arrZ, Color color) {
        super(pixelWriter, arrX, arrY);
        this.arrZ = arrZ;
        this.color = color;
    }

    @Override
    protected InterpolationResult interpolate(int x, int y) {
        float[] barycentricCoordinates = Barycentric.calculate(x, y, arrX, arrY);
        double z = barycentricCoordinates[0] * arrZ[0] +
                barycentricCoordinates[1] * arrZ[1] +
                barycentricCoordinates[2] * arrZ[2];
        return new InterpolationResult(barycentricCoordinates, color, z);
    }

    @Override
    protected void swap(int i, int j) {
        super.swap(i, j);
        double tempZ = arrZ[i];
        arrZ[i] = arrZ[j];
        arrZ[j] = tempZ;
    }
}
