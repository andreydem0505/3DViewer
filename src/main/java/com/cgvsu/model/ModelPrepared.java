package com.cgvsu.model;

import com.cgvsu.render_engine.RenderMode;

import java.awt.image.BufferedImage;

import javafx.scene.paint.Color;

public class ModelPrepared {
    public Model model;
    private RenderMode renderMode;

    private String currentModeCode = "Grid";
    private String name;

    public Color getCurrentColorCode() {
        return currentColorCode;
    }

    public void setCurrentColorCode(Color currentColorCode) {
        this.currentColorCode = currentColorCode;
    }

    private Color currentColorCode = Color.BLACK;
    private BufferedImage texture = null;
    private boolean renderableFlag = true;

    public ModelPrepared(Model model, String name, RenderMode renderModeFactory) {
        this.model = model;
        this.name = name;
        this.renderMode = renderModeFactory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentModeCode() {
        return currentModeCode;
    }

    public void setCurrentModeCode(String currentModeCode) {
        this.currentModeCode = currentModeCode;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(RenderMode renderModeFactory) {
        this.renderMode = renderModeFactory;
    }

    public boolean isRenderableFlag() {
        return renderableFlag;
    }

    public void setRenderableFlag(boolean renderableFlag) {
        this.renderableFlag = renderableFlag;
    }
}
