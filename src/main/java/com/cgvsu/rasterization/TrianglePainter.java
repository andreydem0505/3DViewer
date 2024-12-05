package com.cgvsu.rasterization;

import com.cgvsu.render_engine.PixelWriter;

public abstract class TrianglePainter {
    protected final PixelWriter pixelWriter;
    protected final int[] arrX;
    protected final int[] arrY;

    public TrianglePainter(PixelWriter pixelWriter, int[] arrX, int[] arrY) {
        this.pixelWriter = pixelWriter;
        this.arrX = arrX;
        this.arrY = arrY;
    }

    public void putPixel(int x, int y) {
        InterpolationResult result = interpolate(x, y);
        pixelWriter.putPixel(x, y, result.z, result.color);
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

    protected abstract InterpolationResult interpolate(int x, int y);

    protected void swap(int i, int j) {
        int tempY = arrY[i];
        int tempX = arrX[i];
        arrX[i] = arrX[j];
        arrY[i] = arrY[j];
        arrX[j] = tempX;
        arrY[j] = tempY;
    }
}
