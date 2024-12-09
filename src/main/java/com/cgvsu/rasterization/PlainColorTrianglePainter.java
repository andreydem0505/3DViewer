package com.cgvsu.rasterization;

import com.cgvsu.render_engine.PixelWriter;

import java.awt.*;

public class PlainColorTrianglePainter extends TrianglePainter {
    protected final Color color;

    public PlainColorTrianglePainter(PixelWriter pixelWriter, int[] arrX, int[] arrY, double[] arrZ, Color color) {
        super(pixelWriter, arrX, arrY, arrZ);
        this.color = color;
    }

    @Override
    protected InterpolationResult interpolate(int x, int y) {
        InterpolationResult result = super.interpolate(x, y);
        if (result == null) return null;
        result.color = color;
        return result;
    }
}
