package com.cgvsu.render_engine;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.Arrays;

public class PixelWriter {
    private int[] pixels;
    private PixelBuffer<IntBuffer> pixelBuffer;
    private final ImageView imageView;
    private double[][] zBuffer;
    private int width;
    private int height;

    public PixelWriter(final ImageView imageView) {
        this.imageView = imageView;
        clearScreen((int) imageView.getFitWidth(), (int) imageView.getFitHeight());
    }

    public void clearScreen(int width, int height) {
        if (width != this.width || height != this.height) {
            this.width = width;
            this.height = height;
            IntBuffer buffer = IntBuffer.allocate(width * height);
            pixels = buffer.array();
            pixelBuffer = new PixelBuffer<>(width, height, buffer, PixelFormat.getIntArgbPreInstance());
            zBuffer = new double[height][width];
        }
        for (int y = 0; y < height; y++) {
            Arrays.fill(zBuffer[y], Double.MAX_VALUE);
        }
        Arrays.fill(pixels, 0);
    }

    public void putPixel(final int x, final int y, final double z, final Color color) {
        if (isPixelVisible(x, y, z)) {
            forcePutPixel(x, y, z, color);
        }
    }

    public void forcePutPixel(final int x, final int y, final double z, final Color color) {
        zBuffer[y][x] = z;
        putPixel(x, y, color);
    }

    public boolean isPixelVisible(final int x, final int y, final double z) {
        return y >= 0 && y < zBuffer.length && x >= 0 && x < zBuffer[0].length && z <= zBuffer[y][x];
    }

    private void putPixel(final int x, final int y, final Color color) {
        int colorARGB = color.getAlpha() << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
        pixels[(x % width) + (y * width)] = colorARGB;
    }

    public void draw() {
        WritableImage image = new WritableImage(pixelBuffer);
        imageView.setImage(image);
    }
}
