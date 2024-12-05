package com.cgvsu.render_engine;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class PixelWriter {
    private final Canvas canvas;
    private double[][] zBuffer;
    private javafx.scene.image.PixelWriter pixelWriter;

    public PixelWriter(final Canvas canvas) {
        this.canvas = canvas;
        clearScreen();
    }

    public void clearScreen() {
        int height = (int) canvas.getHeight();
        int width = (int) canvas.getWidth();
        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
        zBuffer = new double[height][width];
        for (int y = 0; y < height; y++) {
            Arrays.fill(zBuffer[y], Double.MAX_VALUE);
        }
        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
    }

    public void putPixel(final int x, final int y, final double z, final Color color) {
        if (y >= 0 && y < zBuffer.length && x >= 0 && x < zBuffer[0].length && z <= zBuffer[y][x]) {
            zBuffer[y][x] = z;
            pixelWriter.setColor(x, y, color);
        }
    }
}
