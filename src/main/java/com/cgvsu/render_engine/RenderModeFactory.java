package com.cgvsu.render_engine;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RenderModeFactory {
    public static RenderMode grid() {
        return new RenderMode(true, null, null, false);
    }

    public static RenderMode gridPlainColor(Color color) {
        return new RenderMode(true, color, null, false);
    }

    public static RenderMode gridPlainColorLightning(Color color) {
        return new RenderMode(true, color, null, true);
    }

    public static RenderMode gridTexture(BufferedImage image) {
        return new RenderMode(true, null, image, false);
    }

    public static RenderMode gridTextureLightning(BufferedImage image) {
        return new RenderMode(true, null, image, true);
    }

    public static RenderMode plainColorLightning(Color color) {
        return new RenderMode(false, color, null, true);
    }

    public static RenderMode texture(BufferedImage image) {
        return new RenderMode(false, null, image, false);
    }

    public static RenderMode textureLightning(BufferedImage image) {
        return new RenderMode(false, null, image, true);
    }
}
