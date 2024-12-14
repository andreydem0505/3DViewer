package com.cgvsu;

import com.cgvsu.math.Linal;
import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.render_engine.PixelWriter;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.RenderModeFactory;
import com.cgvsu.triangulation.Triangulation;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.control.TextField;

import java.awt.*;
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
    private TreeView<String> camerasTree;

    @FXML
    private TextField directionX;

    @FXML
    private TextField directionY;

    @FXML
    private TextField directionZ;

    @FXML
    private TextField cameraX;

    @FXML
    private TextField cameraY;

    @FXML
    private TextField distance;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private ImageView imageView;

    private Model mesh = null;

    private final CamerasController camerasController = new CamerasController(
            new Camera(
                    new Vector2f(Linal.pi / 4, Linal.pi / 4),
                    100f,
                    new Vector3f(0, 0, 0),
                    1.0F,
                    1,
                    0.01F,
                    100)
    );

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> imageView.setFitWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> imageView.setFitHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        PixelWriter pixelWriter = new PixelWriter(imageView);

        RenderEngine renderEngine = new RenderEngine();

        if (camerasController.getCamerasQuantity() == 1) {
            updateCameraTree();
            setCurrent(0);
            System.out.println("lol");
        }

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = imageView.getFitWidth();
            double height = imageView.getFitHeight();

            pixelWriter.clearScreen();
            camerasController.currentCamera.setAspectRatio((float) (height / width));

            if (mesh != null) {
                try {
//                    renderEngine.render(pixelWriter, camerasController.currentCamera, mesh, (int) width, (int) height,
//                            RenderModeFactory.gridPlainColor(Color.BLUE));
//                    renderEngine.render(pixelWriter, camerasController.currentCamera, mesh, (int) width, (int) height,
//                            RenderModeFactory.gridPlainColorLightning(Color.BLUE));
//                    renderEngine.render(pixelWriter, camerasController.currentCamera, mesh, (int) width, (int) height,
//                            RenderModeFactory.plainColorLightning(Color.BLUE));
                    renderEngine.render(pixelWriter, camerasController.currentCamera, mesh, (int) width, (int) height,
                            RenderModeFactory.gridTexture(new File("./models/caracal_texture.png")));
                } catch (IOException e) {
                    // TODO обработать ошибки с файлом текстуры
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        loadModel("./models/caracal_cube.obj");
//        loadModel("./models/CorrectedCubeWithRemovedVertices.obj");
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) imageView.getScene().getWindow());
        if (file == null) {
            return;
        }

        loadModel(file.getAbsolutePath());
    }

    private void loadModel(String path) {
        Path fileName = Path.of(path);

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            mesh.normals = Linal.calculateVerticesNormals(mesh.vertices, mesh.polygons);
            for (Polygon polygon : mesh.polygons)
                Triangulation.convexPolygonTriangulate(polygon);
            // todo: обработка ошибок
        } catch (IOException exception) {
            System.err.println("Failed to load model.\nError: " + exception.getLocalizedMessage());
        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camerasController.currentCamera.moveDistance(-TRANSLATION);
        updateFields();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camerasController.currentCamera.moveDistance(TRANSLATION);
        updateFields();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camerasController.currentCamera.moveRotation(new Vector2f(TRANSLATION / 100, 0));
        updateFields();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camerasController.currentCamera.moveRotation(new Vector2f(-TRANSLATION / 100, 0));
        updateFields();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camerasController.currentCamera.moveRotation(new Vector2f(0, -TRANSLATION / 100));
        updateFields();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camerasController.currentCamera.moveRotation(new Vector2f(0, TRANSLATION / 100));
        updateFields();
    }

    @FXML
    private void addCamera() {
        camerasController.addCamera(
                new Camera(
                        new Vector2f(Linal.pi / 4, Linal.pi / 4),
                        100f,
                        new Vector3f(0, 0, 0),
                        1.0F,
                        1,
                        0.01F,
                        100)
        );
        updateCameraTree();
    }

    private void setCurrent(int index) {
        camerasController.setCurrent(index);
        camerasTree.getSelectionModel().select(index);
        updateFields();
    }

    @FXML
    private void removeCamera() {
        if (camerasController.getCamerasQuantity() == 1) {
            showAlert("Cringe", "Нельзя удалить последнюю камеру");
            return;
        }
        camerasController.removeCamera(camerasTree.getSelectionModel().getSelectedIndex());
        updateCameraTree();
    }

    private void updateFields() {
        distance.setText(String.valueOf(camerasController.currentCamera.getDistance()));

        cameraX.setText(String.valueOf(camerasController.currentCamera.getRotation().x()));
        cameraY.setText(String.valueOf(camerasController.currentCamera.getRotation().y()));

        directionX.setText(String.valueOf(camerasController.currentCamera.getTarget().x()));
        directionY.setText(String.valueOf(camerasController.currentCamera.getTarget().y()));
        directionZ.setText(String.valueOf(camerasController.currentCamera.getTarget().y()));
    }

    private void updateCameraTree() {
        TreeItem<String> root = new TreeItem<>("Cameras");
        camerasTree.setRoot(root);
        for (int i = 0; i < camerasController.getCamerasQuantity(); i++) {
            root.getChildren().add(new TreeItem<>("Camera " + (i + 1)));
        }
        camerasTree.setShowRoot(false);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCameraSelection(MouseEvent event) {
        TreeItem<String> selectedItem = camerasTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = camerasTree.getRoot().getChildren().indexOf(selectedItem);
            if (selectedIndex >= 0) {
                setCurrent(selectedIndex);
            }
        }
    }

    @FXML
    private void updateCameraDirectionAndPosition() {
        camerasController.currentCamera.setTarget(new Vector3f(Float.parseFloat(directionX.getText()), Float.parseFloat(directionX.getText()), Float.parseFloat(directionX.getText())));
        camerasController.currentCamera.setDistance(Float.parseFloat(distance.getText()));
        camerasController.currentCamera.setRotation(new Vector2f(Float.parseFloat(cameraX.getText()), Float.parseFloat(cameraY.getText())));
//        currentCamera.setPosition(parseVector(cameraX.getText(), cameraY.getText(), cameraZ.getText()));
    }
}