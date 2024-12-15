package com.cgvsu.render_engine;

import java.awt.*;
import java.io.File;

public class RenderMode {
    public final boolean grid;
    public final Color color;
    public final File texture;
    public final boolean light;

    RenderMode(boolean grid, Color color, File texture, boolean light) {
        this.grid = grid;
        this.color = color;
        this.texture = texture;
        this.light = light;
    }

    public Color getColor() {
        return color;
    }

    public File getTexture() {
        return texture;
    }
}
