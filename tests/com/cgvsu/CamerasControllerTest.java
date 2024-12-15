package com.cgvsu;

import com.cgvsu.gui.CamerasController;
import com.cgvsu.math.Linal;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.render_engine.Camera;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CamerasControllerTest {
    private Camera getCamera() {
        return new Camera(
                new Vector2f(Linal.pi / 4, Linal.pi / 4),
                100f,
                new Vector3f(0, 0, 0),
                1.0F,
                1,
                0.01F,
                100);
    }

    @Test
    public void testInitialization() {
        Camera camera = getCamera();
        CamerasController controller = new CamerasController(camera);
        Assertions.assertEquals(camera, controller.currentCamera);
        Assertions.assertEquals(1, controller.getCamerasQuantity());
    }

    @Test
    public void testCameraAddition() {
        Camera camera = getCamera();
        CamerasController controller = new CamerasController(camera);
        controller.addCamera(getCamera());
        Assertions.assertEquals(camera, controller.currentCamera);
        Assertions.assertEquals(2, controller.getCamerasQuantity());
    }

    @Test
    public void testSetCurrent() {
        Camera camera = getCamera();
        CamerasController controller = new CamerasController(camera);
        camera = getCamera();
        controller.addCamera(camera);
        controller.setCurrent(1);
        Assertions.assertEquals(camera, controller.currentCamera);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 2})
    public void testSetCurrentIncorrectValue(int number) {
        Camera camera = getCamera();
        CamerasController controller = new CamerasController(camera);
        controller.addCamera(getCamera());
        controller.setCurrent(number);
        Assertions.assertEquals(camera, controller.currentCamera);
    }

    @Test
    public void testRemoveCamera() {
        Camera camera = getCamera();
        CamerasController controller = new CamerasController(camera);
        controller.addCamera(getCamera());
        controller.removeCamera(1);
        Assertions.assertEquals(camera, controller.currentCamera);
        Assertions.assertEquals(1, controller.getCamerasQuantity());
    }

    @Test
    public void testRemoveCurrentCamera() {
        Camera camera = getCamera();
        CamerasController controller = new CamerasController(camera);
        controller.addCamera(getCamera());
        controller.setCurrent(1);
        controller.removeCamera(1);
        Assertions.assertEquals(camera, controller.currentCamera);
        Assertions.assertEquals(1, controller.getCamerasQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 2})
    public void testRemoveWithIncorrectIndex(int number) {
        Camera camera = getCamera();
        CamerasController controller = new CamerasController(camera);
        controller.addCamera(getCamera());
        controller.removeCamera(number);
        Assertions.assertEquals(camera, controller.currentCamera);
        Assertions.assertEquals(2, controller.getCamerasQuantity());
    }

    @Test
    public void testRemoveTheOnlyCamera() {
        Camera camera = getCamera();
        CamerasController controller = new CamerasController(camera);
        controller.removeCamera(0);
        Assertions.assertEquals(camera, controller.currentCamera);
        Assertions.assertEquals(1, controller.getCamerasQuantity());
    }
}
