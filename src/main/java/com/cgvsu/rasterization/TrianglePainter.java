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

    public abstract void putPixel(int x, int y);
}
