package com.cgvsu.gui;

import com.cgvsu.model.ModelPrepared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelController {
    public ModelPrepared currentModel;

    public List<ModelPrepared> getModelList() {
        return modelList;
    }

    private final List<ModelPrepared> modelList;
    private Set<String> namesSet = new HashSet<>();

    public ModelController(ModelPrepared modelPrepared) {
        modelList = new ArrayList<>();
        modelList.add(modelPrepared);
        currentModel = modelPrepared;
    }

    public void addNameToNameSet(String name) {
        namesSet.add(name);
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
            namesSet.remove(modelList.get(index).getName());
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

    public Set<String> getNamesSet() {
        return namesSet;
    }
}
