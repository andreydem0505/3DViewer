package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;

import java.util.ArrayList;
import java.util.List;

public class ModelController {
    public ModelPrepared currentModel;

    public List<ModelPrepared> getModelList() {
        return modelList;
    }

    private final List<ModelPrepared> modelList;

    public ModelController(ModelPrepared modelPrepared) {
        modelList = new ArrayList<>();
        modelList.add(modelPrepared);
        currentModel = modelPrepared;
    }

    public ModelController() {
        modelList = new ArrayList<>();
        currentModel = null;
    }

    public void addModel(ModelPrepared modelPrepared) {
        modelList.add(modelPrepared);

    }
    public void setCurrent(int index) {
        if (index >= 0 && index < modelList.size())
            currentModel = modelList.get(index);
    }
    public void removeModel(int index) {
        if (index >= 0 && index < modelList.size()) {
            modelList.remove(index);
            if (getModelsQuantity() > 0) {
                currentModel = modelList.get(0);
            }
            else {
                currentModel = null;
            }
        }
    }

    public int getModelsQuantity() {
        return modelList.size();
    }

    public boolean hasRenderableModels() {
        if (modelList.size() > 0) {
            for (ModelPrepared modelPrepared: modelList) {
                if (modelPrepared.isRenderableFlag()) {
                    return true;
                }
            }
        }
        return false;
    }
}
