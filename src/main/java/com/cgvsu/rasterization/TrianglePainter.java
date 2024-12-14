package com.cgvsu.rasterization;

import com.cgvsu.math.Barycentric;
import com.cgvsu.math.Linal;
import com.cgvsu.render_engine.PixelWriter;

public class TrianglePainter {
    protected final PixelWriter pixelWriter;
    protected final int[] arrX;
    protected final int[] arrY;
    protected final double[] arrZ;

    public TrianglePainter(PixelWriter pixelWriter, int[] arrX, int[] arrY, double[] arrZ) {
        this.pixelWriter = pixelWriter;
        this.arrX = arrX;
        this.arrY = arrY;
        this.arrZ = arrZ;
    }

    public void putPixel(int x, int y) {
        InterpolationResult result = interpolate(x, y);
        if (result == null) return;
        for (float coordinate : result.barycentricCoordinates) {
            if (Math.abs(coordinate) > Linal.eps) {
                pixelWriter.forcePutPixel(x, y, result.z, result.color);
                break;
            }
        }
    }

    protected void sort() {
        if (arrY[0] > arrY[1]) {
            swap(0, 1);
        }
        if (arrY[1] > arrY[2]) {
            swap(1, 2);
        }
        if (arrY[0] > arrY[1]) {
            swap(0, 1);
        }
    }

    protected InterpolationResult interpolate(int x, int y) {
        float[] barycentricCoordinates = Barycentric.calculate(x, y, arrX, arrY);
        double z = Barycentric.getDouble(barycentricCoordinates, arrZ);
        if (!pixelWriter.isPixelVisible(x, y, z))
            return null;
        return new InterpolationResult(barycentricCoordinates, null, z);
    }

    protected void swap(int i, int j) {
        int tempY = arrY[i];
        int tempX = arrX[i];
        double tempZ = arrZ[i];
        arrX[i] = arrX[j];
        arrY[i] = arrY[j];
        arrZ[i] = arrZ[j];
        arrX[j] = tempX;
        arrY[j] = tempY;
        arrZ[j] = tempZ;
    }
}
