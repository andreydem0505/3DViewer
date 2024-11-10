package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dementiev Andrey
 */
public class CamerasController {
    public Camera currentCamera;
    private final List<Camera> cameras;

    public CamerasController(Camera camera) {
        cameras = new ArrayList<>();
        cameras.add(camera);
        currentCamera = camera;
    }

    public void addCamera(Camera camera) {
        cameras.add(camera);
    }

    public void setCurrent(int index) {
        if (index >= 0 && index < cameras.size())
            currentCamera = cameras.get(index);
    }

    public void removeCamera(int index) {
        if (index >= 0 && index < cameras.size() && cameras.size() > 1)
            cameras.remove(index);
    }

    public int getCamerasQuantity() {
        return cameras.size();
    }
}
