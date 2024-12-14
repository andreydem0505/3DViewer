package com.cgvsu.render_engine;

import java.awt.*;
import java.io.File;

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

    public static RenderMode gridTexture(File file) {
        return new RenderMode(true, null, file, false);
    }

    public static RenderMode gridTextureLightning(File file) {
        return new RenderMode(true, null, file, true);
    }

    public static RenderMode plainColorLightning(Color color) {
        return new RenderMode(false, color, null, true);
    }

    public static RenderMode texture(File file) {
        return new RenderMode(false, null, file, false);
    }

    public static RenderMode textureLightning(File file) {
        return new RenderMode(false, null, file, true);
    }
}
