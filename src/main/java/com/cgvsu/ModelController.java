package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;

import java.util.ArrayList;
import java.util.List;

public class ModelController {
    public Model currentModel;

    public List<Model> getModelList() {
        return modelList;
    }

    private final List<Model> modelList;

    public ModelController(Model model) {
        modelList = new ArrayList<>();
        modelList.add(model);
        currentModel = model;
    }

    public ModelController() {
        modelList = new ArrayList<>();
        currentModel = null;
    }

    public void addModel(Model model) {
        modelList.add(model);
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
}
