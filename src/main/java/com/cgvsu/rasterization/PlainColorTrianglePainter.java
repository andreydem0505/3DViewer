package com.cgvsu.rasterization;

import com.cgvsu.math.Barycentric;
import com.cgvsu.render_engine.PixelWriter;
import javafx.scene.paint.Color;

public class PlainColorTrianglePainter extends TrianglePainter {
    private final double[] arrZ;
    private final Color color;

    public PlainColorTrianglePainter(PixelWriter pixelWriter, int[] arrX, int[] arrY, double[] arrZ, Color color) {
        super(pixelWriter, arrX, arrY);
        this.arrZ = arrZ;
        this.color = color;
        sort();
    }

    @Override
    public void putPixel(int x, int y) {
        double[] barycentricCoordinates = Barycentric.calculate(x, y, arrX, arrY);
        double z = barycentricCoordinates[0] * arrZ[0] +
                barycentricCoordinates[1] * arrZ[1] +
                barycentricCoordinates[2] * arrZ[2];
        pixelWriter.putPixel(x, y, z, color);
    }

    protected void sort() {
        if (arrY[0] > arrY[1]) {
            swap(arrX, arrY, arrZ, 0, 1);
        }
        if (arrY[1] > arrY[2]) {
            swap(arrX, arrY, arrZ, 1, 2);
        }
        if (arrY[0] > arrY[1]) {
            swap(arrX, arrY, arrZ, 0, 1);
        }
    }

    private void swap(int[] x, int[] y, double[] z, int i, int j) {
        int tempY = y[i];
        int tempX = x[i];
        double tempZ = z[i];
        x[i] = x[j];
        y[i] = y[j];
        z[i] = z[j];
        x[j] = tempX;
        y[j] = tempY;
        z[j] = tempZ;
    }
}
