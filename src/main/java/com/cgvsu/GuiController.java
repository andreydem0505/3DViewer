package com.cgvsu;

import com.cgvsu.math.Linal;
import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.render_engine.CamerasController;
import com.cgvsu.render_engine.PixelWriter;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.triangulation.Triangulation;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;

import com.cgvsu.model.Model;
import com.cgvsu.io.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 2F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private final CamerasController camerasController = new CamerasController(
            new Camera(
                    new Vector3f(0, 0, 100),
                    new Vector3f(0, 0, 0),
                    1.0F, 1, 0.01F, 100)
    );

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        PixelWriter pixelWriter = new PixelWriter(canvas);

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            pixelWriter.clearScreen();
            camerasController.currentCamera.setAspectRatio((float) (height / width));

            if (mesh != null) {
                RenderEngine.render(pixelWriter, camerasController.currentCamera, mesh, (int) width, (int) height);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            mesh.normals = Linal.calculateVerticesNormals(mesh.vertices, mesh.polygons);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camerasController.currentCamera.movePosition(new Vector3f(0, 0, -TRANSLATION));
//        mesh.position.add(new Vector3f(0f, TRANSLATION, 0f));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camerasController.currentCamera.movePosition(new Vector3f(0, 0, TRANSLATION));
//        mesh.position.add(new Vector3f(0f, -TRANSLATION, 0f));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camerasController.currentCamera.movePosition(new Vector3f(TRANSLATION, 0, 0));
//        mesh.position.add(new Vector3f(TRANSLATION, 0f, 0f));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camerasController.currentCamera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
//        mesh.position.add(new Vector3f(-TRANSLATION, 0f, 0f));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camerasController.currentCamera.movePosition(new Vector3f(0, TRANSLATION, 0));
//        mesh.rotation.add(new Vector3f(0f, 0f, TRANSLATION / 100));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camerasController.currentCamera.movePosition(new Vector3f(0, -TRANSLATION, 0));
//        mesh.rotation.add(new Vector3f(0f, 0f, -TRANSLATION / 100));
    }
}