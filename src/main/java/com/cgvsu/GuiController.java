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
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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
    private TreeView<String> objectsTree;

    @FXML
    private TextField directionX;

    @FXML
    private TextField directionY;

    @FXML
    private TextField directionZ;

    @FXML
    private TextField cameraX;

    @FXML
    private TextField redColor;

    @FXML
    private TextField greenColor;

    @FXML
    private TextField blueColor;

    @FXML
    private TextField cameraY;

    @FXML
    private TextField distance;

    @FXML
    private ChoiceBox choiceBoxRenderMode;

//    @FXML
//    private ChoiceBox choiceBoxColor;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private ImageView imageView;


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

    private final ModelController modelController = new ModelController();

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> imageView.setFitWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> imageView.setFitHeight(newValue.doubleValue()));

        choiceBoxRenderMode.getItems().addAll("Inactive", "Grid", "GridColor", "GridColorLight", "GridTexture", "GridTextureLight", "ColorLight", "Texture", "TextureLight");
        choiceBoxRenderMode.setValue("Grid");

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        PixelWriter pixelWriter = new PixelWriter(imageView);

        RenderEngine renderEngine = new RenderEngine();

        if (camerasController.getCamerasQuantity() == 1) {
            updateCameraTree();
            setCurrentCamera(0);
            System.out.println("lol");
        }

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = imageView.getFitWidth();
            double height = imageView.getFitHeight();

            pixelWriter.clearScreen();
            camerasController.currentCamera.setAspectRatio((float) (height / width));

            if (modelController.hasRenderableModels()) {
                for (ModelPrepared modelPrepared : modelController.getModelList()) {
                    try {
                        if (modelPrepared.isRenderableFlag()) {
                            renderEngine.render(pixelWriter, camerasController.currentCamera, modelPrepared.model, (int) width, (int) height,
                                    modelPrepared.getRenderMode());
                        }
                    } catch (IOException e) {
                        // TODO обработать ошибки с файлом текстуры
                    }
                }
            } else {
                imageView.setImage(null); // Я сделал кринжовый фикс?
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        loadModel("./models/caracal_cube.obj");
        updateModelTree();
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

    @FXML
    private void loadTextureForModel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texture (*.png)", "*.png"));
        fileChooser.setTitle("Load texture");

        File file = fileChooser.showOpenDialog((Stage) imageView.getScene().getWindow());
        if (file == null) {
            return;
        }

        modelController.currentModel.setTexture(file);
        handleRenderChoiceBoxChoice();
    }

    private void loadModel(String path) {
        Path fileName = Path.of(path);
        Model newModel;
        try {
            String fileContent = Files.readString(fileName);
            newModel = ObjReader.read(fileContent);
            newModel.normals = Linal.calculateVerticesNormals(newModel.vertices, newModel.polygons);
            for (Polygon polygon : newModel.polygons)
                Triangulation.convexPolygonTriangulate(polygon);
            modelController.addModel(new ModelPrepared(newModel, RenderModeFactory.grid()));
            if (modelController.getModelsQuantity() == 1) {
                modelController.currentModel = modelController.getModelList().get(0);
            }
            // todo: обработка ошибок
        } catch (IOException exception) {
            System.err.println("Failed to load model.\nError: " + exception.getLocalizedMessage());
        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camerasController.currentCamera.moveDistance(-TRANSLATION);
        updateCameraFields();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camerasController.currentCamera.moveDistance(TRANSLATION);
        updateCameraFields();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camerasController.currentCamera.moveRotation(new Vector2f(TRANSLATION / 100, 0));
        updateCameraFields();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camerasController.currentCamera.moveRotation(new Vector2f(-TRANSLATION / 100, 0));
        updateCameraFields();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camerasController.currentCamera.moveRotation(new Vector2f(0, -TRANSLATION / 100));
        updateCameraFields();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camerasController.currentCamera.moveRotation(new Vector2f(0, TRANSLATION / 100));
        updateCameraFields();
    }

    @FXML
    private void addModelToTheScene() {
        onOpenModelMenuItemClick();
        updateModelTree();
    }

    private void updateModelTree() {
        TreeItem<String> root = new TreeItem<>("Models");
        objectsTree.setRoot(root);
        for (int i = 0; i < modelController.getModelsQuantity(); i++) {
            root.getChildren().add(new TreeItem<>("Model " + (i + 1)));
        }
        objectsTree.setShowRoot(false);
        updateChoiceBoxes();
    }

    @FXML
    private void removeModel() {
        modelController.removeModel(objectsTree.getSelectionModel().getSelectedIndex());
        updateModelsTree();
    }

    @FXML
    private void handleModelSelection(MouseEvent event) {
        TreeItem<String> selectedItem = objectsTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = objectsTree.getRoot().getChildren().indexOf(selectedItem);
            if (selectedIndex >= 0) {
                setCurrentModel(selectedIndex);
            }
        }
        updateChoiceBoxes();
    }

    private void updateChoiceBoxes() {
//        System.out.println("Set color " + modelController.currentModel.getCurrentColorCode());
        choiceBoxRenderMode.setValue(modelController.currentModel.getCurrentModeCode());
        colorPicker.setValue(modelController.currentModel.getCurrentColorCode());
    }

    @FXML
    private void handleRenderChoiceBoxChoice() {
        if (modelController.currentModel != null) {
            if (choiceBoxRenderMode.getValue().toString().equals("Inactive")) {
                modelController.currentModel.setRenderableFlag(false);
            } else if (choiceBoxRenderMode.getValue().toString().equals("Grid")) {
                modelController.currentModel.setRenderableFlag(true);
                modelController.currentModel.setRenderMode(RenderModeFactory.grid());
            } else if (choiceBoxRenderMode.getValue().toString().equals("GridColor")) {
                modelController.currentModel.setRenderableFlag(true);
                modelController.currentModel.setRenderMode(RenderModeFactory.gridPlainColor(convertColor(modelController.currentModel.getCurrentColorCode())));
            } else if (choiceBoxRenderMode.getValue().toString().equals("GridColorLight")) {
                modelController.currentModel.setRenderableFlag(true);
                modelController.currentModel.setRenderMode(RenderModeFactory.gridPlainColorLightning(convertColor(modelController.currentModel.getCurrentColorCode())));
            } else if (choiceBoxRenderMode.getValue().toString().equals("GridTexture")) {
                modelController.currentModel.setRenderableFlag(true);
                modelController.currentModel.setRenderMode(RenderModeFactory.gridTexture(modelController.currentModel.getTexture()));
            } else if (choiceBoxRenderMode.getValue().toString().equals("GridTextureLight")) {
                modelController.currentModel.setRenderableFlag(true);
                modelController.currentModel.setRenderMode(RenderModeFactory.gridTextureLightning(modelController.currentModel.getTexture()));
            } else if (choiceBoxRenderMode.getValue().toString().equals("ColorLight")) {
                modelController.currentModel.setRenderableFlag(true);
                modelController.currentModel.setRenderMode(RenderModeFactory.plainColorLightning(convertColor(modelController.currentModel.getCurrentColorCode())));
            } else if (choiceBoxRenderMode.getValue().toString().equals("Texture")) {
                modelController.currentModel.setRenderableFlag(true);
                modelController.currentModel.setRenderMode(RenderModeFactory.texture(modelController.currentModel.getTexture()));
            } else if (choiceBoxRenderMode.getValue().toString().equals("TextureLight")) {
                modelController.currentModel.setRenderableFlag(true);
                modelController.currentModel.setRenderMode(RenderModeFactory.textureLightning(modelController.currentModel.getTexture()));
            }
            modelController.currentModel.setCurrentModeCode(choiceBoxRenderMode.getValue().toString());
        }
    }

    private void setCurrentModel(int index) {
        modelController.setCurrent(index);
        objectsTree.getSelectionModel().select(index);
    }

    private Color convertColor(javafx.scene.paint.Color colorCode) {
        java.awt.Color awtColor = new java.awt.Color(
                (float) colorCode.getRed(),
                (float) colorCode.getGreen(),
                (float) colorCode.getBlue(),
                (float) colorCode.getOpacity());
        return awtColor;
    }

    @FXML
    private void handleColorChoiceBox() {
        try {
            modelController.currentModel.setCurrentColorCode(colorPicker.getValue());
        } catch (Exception ignored) {}
        System.out.println(colorPicker.getValue());
        handleRenderChoiceBoxChoice();
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

    private void setCurrentCamera(int index) {
        camerasController.setCurrent(index);
        camerasTree.getSelectionModel().select(index);
        updateCameraFields();
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

    private void updateCameraFields() {
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

    private void updateModelsTree() {
        TreeItem<String> root = new TreeItem<>("Models");
        objectsTree.setRoot(root);
        for (int i = 0; i < modelController.getModelsQuantity(); i++) {
            root.getChildren().add(new TreeItem<>("Model " + (i + 1)));
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
                setCurrentCamera(selectedIndex);
            }
        }
    }

    @FXML
    private void updateCameraDirectionAndPosition() {
        camerasController.currentCamera.setTarget(new Vector3f(Float.parseFloat(directionX.getText()), Float.parseFloat(directionY.getText()), Float.parseFloat(directionZ.getText())));
        camerasController.currentCamera.setDistance(Float.parseFloat(distance.getText()));
        camerasController.currentCamera.setRotation(new Vector2f(Float.parseFloat(cameraX.getText()), Float.parseFloat(cameraY.getText())));
    }
}