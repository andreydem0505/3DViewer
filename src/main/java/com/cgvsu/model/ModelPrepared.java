package com.cgvsu.model;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.RenderMode;

import java.awt.*;
import java.io.File;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class ModelPrepared {
    public Model model;
    private RenderMode renderMode;

    private String currentModeCode = "Grid";

    public Color getCurrentColorCode() {
        return currentColorCode;
    }

    public void setCurrentColorCode(Color currentColorCode) {
        this.currentColorCode = currentColorCode;
    }

    private Color currentColorCode = Color.BLACK;
    private File texture = null;
    private boolean renderableFlag = true;

    public ModelPrepared(Model model, RenderMode renderModeFactory) {
        this.model = model;
        this.renderMode = renderModeFactory;
    }

    public String getCurrentModeCode() {
        return currentModeCode;
    }

    public void setCurrentModeCode(String currentModeCode) {
        this.currentModeCode = currentModeCode;
    }

    public File getTexture() {
        return texture;
    }

    public void setTexture(File texture) {
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