package com.cgvsu.rasterization;

import com.cgvsu.nmath.Vector2f;
import com.cgvsu.render_engine.PixelWriter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextureTrianglePainter extends TrianglePainter {
    private final Vector2f[] textureVertices;
    private final BufferedImage image;
    private final int width;
    private final int height;

    public TextureTrianglePainter(PixelWriter pixelWriter, int[] arrX, int[] arrY, double[] arrZ,
                                  Vector2f[] textureVertices, BufferedImage image) {
        super(pixelWriter, arrX, arrY, arrZ);
        this.textureVertices = textureVertices;
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    @Override
    protected InterpolationResult interpolate(int x, int y) {
        InterpolationResult interpolation = super.interpolate(x, y);
        if (interpolation == null) return null;

        double textureX = interpolation.barycentricCoordinates[0] * textureVertices[0].x() +
                interpolation.barycentricCoordinates[1] * textureVertices[1].x() +
                interpolation.barycentricCoordinates[2] * textureVertices[2].x();
        double textureY = interpolation.barycentricCoordinates[0] * textureVertices[0].y() +
                interpolation.barycentricCoordinates[1] * textureVertices[1].y() +
                interpolation.barycentricCoordinates[2] * textureVertices[2].y();

        try {
            interpolation.color = new Color(image.getRGB(
                    (int) (textureX * width),
                    (int) ((1 - textureY) * height))
            );
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return interpolation;
    }

    @Override
    protected void swap(int i, int j) {
        super.swap(i, j);
        Vector2f temp = textureVertices[i];
        textureVertices[i] = textureVertices[j];
        textureVertices[j] = temp;
    }
}
