package com.cgvsu.render_engine;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RenderMode {
    public final boolean grid;
    public final Color color;
    public final BufferedImage texture;
    public final boolean light;

    RenderMode(boolean grid, Color color, BufferedImage texture, boolean light) {
        this.grid = grid;
        this.color = color;
        this.texture = texture;
        this.light = light;
    }

    public Color getColor() {
        return color;
    }

    public BufferedImage getTexture() {
        return texture;
    }
}
