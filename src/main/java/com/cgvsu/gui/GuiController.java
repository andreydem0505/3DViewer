package com.cgvsu.gui;

import com.cgvsu.Configs;
import com.cgvsu.animation.AnimationController;
import com.cgvsu.animation.Frame;
import com.cgvsu.animation.ModelAnimation;
import com.cgvsu.animation.State;
import com.cgvsu.io.animationreader.AnimationReader;
import com.cgvsu.io.animationwriter.AnimationWriter;
import com.cgvsu.io.objreader.ObjReader;
import com.cgvsu.io.objwriter.ObjWriter;
import com.cgvsu.math.Linal;
import com.cgvsu.model.ModelPrepared;
import com.cgvsu.model.Polygon;
import com.cgvsu.model_modification.PolygonRemover;
import com.cgvsu.model_modification.VertexRemoverNextGen;
import com.cgvsu.nmath.Matrix4x4;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.nmath.Vector4f;
import com.cgvsu.rasterization.Lighting;
import com.cgvsu.render_engine.*;
import com.cgvsu.triangulation.Triangulation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.cgvsu.model.Model;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class GuiController {

    final private float TRANSLATION = 2F;

    @FXML
    private CheckBox musicLoop;

    @FXML
    private TreeView<String> camerasTree;

    @FXML
    private TreeView<String> framesTree;

    @FXML
    private TreeView<String> objectsTree;

    @FXML
    private TextField directionX;

    @FXML
    private TextField scaleX;

    @FXML
    private TextField scaleY;

    @FXML
    private TextField verticesToDeleteTextField;

    @FXML
    private TextField polygonsToDeleteTextField;

    @FXML
    private TextField scaleZ;

    @FXML
    private TextField rotationX;

    @FXML
    private TextField rotationY;

    @FXML
    private TextField rotationZ;

    @FXML
    private TextField positionX;

    @FXML
    private TextField positionY;

    @FXML
    private TextField positionZ;

    @FXML
    private TextField directionY;
    @FXML
    private TextField lightingCoeffTextField;

    @FXML
    private TextField directionZ;

    @FXML
    private TextField cameraX;

    @FXML
    private TextField cameraY;

    @FXML
    private TextField distance;

    @FXML
    private TextField frameDuration;

    @FXML
    private CheckBox gridCheckbox;
    @FXML
    private CheckBox colorCheckbox;
    @FXML
    private CheckBox textureCheckbox;
    @FXML
    private CheckBox lightCheckbox;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private ImageView imageView;
    @FXML
    private Label initialVectors;
    @FXML
    private Label destinationVectors;
    @FXML
    private Label durationLabel;

    private volatile boolean musicPlaying = false;
    private boolean playAnimationFlag = false;
    private boolean loopFlag = false;
    private ExecutorService service = Executors.newSingleThreadExecutor();
    private Clip clip;
    private AnimationController animationController;

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

        initializeFields();

        Platform.runLater(() -> {
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> shutdownExecutorService());
            switchThemeToLight();
        });

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        imageView.setPickOnBounds(true); //schizophrenia, don't touch!
        imageView.setOnDragDetected(e -> {
            imageView.startFullDrag();
        });

        PixelWriter pixelWriter = new PixelWriter(imageView);

        RenderEngine renderEngine = new RenderEngine(pixelWriter);

        initializeCamerasController();

        animationController = new AnimationController();

        KeyFrame frame = new KeyFrame(Duration.millis(Configs.FRAME_TIME), event -> {
            double width = imageView.getFitWidth();
            double height = imageView.getFitHeight();

            pixelWriter.clearScreen((int) width, (int) height);
            camerasController.currentCamera.setAspectRatio((float) (height / width));

            if (playAnimationFlag && !animationController.isOver()) {
                animationController.animate();
                updateModelTransformation();
            } else if (loopFlag && animationController.isOver()) {
                animationController.reset();
            }

            if (modelController.hasRenderableModels()) {
                for (ModelPrepared modelPrepared : modelController.getModelList()) {
                    if (modelPrepared.isRenderableFlag()) {
                        renderEngine.render(camerasController.currentCamera,
                                modelPrepared.model, (int) width, (int) height, modelPrepared.getRenderMode());
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
        updateModelTransformation();
//        ModelAnimation animation = new ModelAnimation();
//        animation.addFrame(
//                new Frame(
//                        new State(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)),
//                        new State(new Vector3f(8, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)),
//                        TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)
//                )
//        );
//        animation.addFrame(
//                new Frame(
//                        new State(new Vector3f(8, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)),
//                        new State(new Vector3f(8, 0, 0), new Vector3f(Linal.pi / 4, 0, 0), new Vector3f(1, 1, 1)),
//                        TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS)
//                )
//        );
//        animationController.animations.put(modelController.currentModel, animation);
//        updateAnimationTree();
//        try {
            //todo remove
            //illustration of writing and reading of the default animation
//            AnimationWriter.writeAnimations(animationController.animations, "animation_dump.json");

//            Map<String, ModelPrepared> models = modelController.getModelList().stream().collect(Collectors.toMap(
//                    ModelPrepared::getName,
//                    m -> m
//            ));
//            AnimationReader.readAnimations(models, "animation_dump.json");
//        }catch (IOException e){
//            System.err.println("Unable to save animation dump." + e.getLocalizedMessage());
//        }
    }

    private void initializeCamerasController() {
        if (camerasController.getCamerasQuantity() == 1) {
            updateCameraTree();
            setCurrentCamera(0);
        }
    }

    private void initializeFields() {
        gridCheckbox.setSelected(true);
        lightCheckbox.setDisable(true);
        scaleX.setText("1");
        scaleY.setText("1");
        scaleZ.setText("1");
        rotationX.setText("0");
        rotationY.setText("0");
        rotationZ.setText("0");
        positionX.setText("0");
        positionY.setText("0");
        positionZ.setText("0");
        lightingCoeffTextField.setText(Float.toString(Lighting.k));
    }

    @FXML
    private void switchThemeToDark() {
        Scene scene = anchorPane.getScene();

        if (scene == null) {
            return;
        }

        File style = new File("./styles/darker-theme.css");

        if (!style.exists()) {
            showError("Error", "Couldn`t find darker-theme.css file");
            return;
        }

        anchorPane.getStylesheets().clear();

        try {
            scene.getStylesheets().add(style.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            showError("Error", "Couldn`t find darker-theme.css file");
        }
    }

    @FXML
    private void switchThemeToLight() {
        Scene scene = anchorPane.getScene();
        if (scene == null) {
            return;
        }

        File style = new File("./styles/lighter-theme.css");

        if (!style.exists()) {
            showError("Error", "Couldn`t find darker-theme.css file");
            return;
        }

        scene.getStylesheets().clear();

        try {
            scene.getStylesheets().add(style.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            showError("Error", "Couldn`t find lighter-theme.css file");
        }
    }

    @FXML
    private void handleNormalsMenuItem() {
        modelController.currentModel.model.normals = Linal.calculateVerticesNormals(modelController.currentModel.model.vertices, modelController.currentModel.model.polygons);
    }

    @FXML
    private void saveModelFile() {
        ObjWriter objWriter = new ObjWriter();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(modelController.currentModel.getName());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save model");

        File file = fileChooser.showSaveDialog((Stage) imageView.getScene().getWindow());

        if (file == null) {
            return;
        }

        String filename = file.getAbsolutePath();

        try {
            objWriter.write(modelController.currentModel.model, filename);
        } catch (Exception e) {
            showError("Error", "Error while writing file");
        }
    }

    @FXML
    private void saveModifiedModelFile() {
        ObjWriter objWriter = new ObjWriter();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save model");

        File file = fileChooser.showSaveDialog((Stage) imageView.getScene().getWindow());

        if (file == null) {
            return;
        }

        String filename = file.getAbsolutePath();

        try {
            objWriter.write(getTransformedModel(modelController.currentModel.model), filename);
        } catch (Exception e) {
            showError("Error", "Error while writing file");
        }
    }

    @FXML
    private void loadTextureForModel() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texture (*.png)", "*.png"));
        fileChooser.setTitle("Load texture");

        File file = fileChooser.showOpenDialog((Stage) imageView.getScene().getWindow());
        if (file == null) {
            return;
        }

        modelController.currentModel.setTexture(ImageIO.read(file));
        handleRenderChoiceBoxChoice();
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
        updateModelTree();
        updateModelTransformation();
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
            String modelName = getModelName(fileName);
            ModelPrepared loadedModel = new ModelPrepared(newModel, modelName, RenderModeFactory.grid());
            modelController.addModel(loadedModel);
            modelController.addNameToNameSet(modelName);
            animationController.animations.put(loadedModel, new ModelAnimation());
            if (modelController.getModelsQuantity() >= 1) {
                modelController.currentModel = modelController.getModelList().get(modelController.getModelsQuantity() - 1);
            }
        } catch (IOException exception) {
            showError("Error loading the model","Failed to load model.\nError: " + exception.getLocalizedMessage());
        }
    }

    private String getModelName(Path fileName) {
        String modelName = fileName.getFileName().toString();
        if (modelController.getNamesSet().contains(modelName)) {
            int counter = 0;
            while (modelController.getNamesSet().contains(modelName)) {
                modelName = modelName.substring(0, (modelName.length() - 4 - (counter > 0 ? String.valueOf(counter).length() : 0))) + (counter + 1) + ".obj";
                counter++;
            }
        }
        return modelName;
    }

    private Model getTransformedModel(Model model) {
        Model newModel = new Model();
        newModel.vertices = new ArrayList<>();

        Matrix4x4 modelMatrix = model.getModelMatrix();
        for (Vector3f vertex : model.vertices){
            Vector4f vertex4 = new Vector4f(vertex.x(), vertex.y(), vertex.z(), 1f);
            vertex4 = modelMatrix.multiplyMV(vertex4);

            newModel.vertices.add(new Vector3f(vertex4.x(), vertex4.y(), vertex.z()));
        }
        newModel.textureVertices = new ArrayList<>(model.textureVertices);
        newModel.normals = new ArrayList<>(model.normals);
        newModel.polygons = new ArrayList<>(model.polygons);
        newModel.groups = new ArrayList<>(model.groups);

        return newModel;
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
        camerasController.currentCamera.moveRotation(new Vector2f(-TRANSLATION / 100, 0));
        updateCameraFields();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camerasController.currentCamera.moveRotation(new Vector2f(TRANSLATION / 100, 0));
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

    float prevX;
    float prevY;
    boolean dragging = false;
    @FXML
    public void handleCameraDragStarted(MouseEvent mouseEvent){
        prevX = (float) mouseEvent.getX();
        prevY = (float) mouseEvent.getY();
        dragging = true;
    }

    @FXML
    public void handleCameraDragEnded(MouseEvent mouseEvent){
        dragging = false;
    }

    @FXML
    public void handleCameraDrag(MouseEvent mouseEvent){
        if (!dragging)
            return;

        float currX = (float) mouseEvent.getX();
        float currY = (float) mouseEvent.getY();

        float deltax = currX - prevX;
        float deltay = currY - prevY;

        camerasController.currentCamera.moveRotation(new Vector2f(-deltax / 100, -deltay / 100));
        updateCameraFields();

        prevX = currX;
        prevY = currY;
    }

    @FXML
    public void handleCameraScroll(ScrollEvent mouseEvent){
        camerasController.currentCamera.moveDistance((float) -mouseEvent.getDeltaY() / 10);
        updateCameraFields();
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
            showWarning("Last camera", "U can`t delete the last camera");
            return;
        }
        camerasController.removeCamera(camerasTree.getSelectionModel().getSelectedIndex());
        updateCameraTree();
    }

    private void updateCameraFields() {
        distance.setText(String.valueOf(camerasController.currentCamera.getDistance()));

        cameraX.setText(String.valueOf(camerasController.currentCamera.getRotation().x() / Linal.pi * 180));
        cameraY.setText(String.valueOf(camerasController.currentCamera.getRotation().y() / Linal.pi * 180));

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

    @FXML
    private void handleLightingCoefChange() {
        try {
            if (Float.parseFloat(lightingCoeffTextField.getText()) > 1) {
                Lighting.k = 1;
                lightingCoeffTextField.setText("1");
                showWarning("Wrong input", "Maximum coefficient is 1");
            } else if (Float.parseFloat(lightingCoeffTextField.getText()) <= 0.009) {
                Lighting.k = 0.01f;
                lightingCoeffTextField.setText("0.01");
                showWarning("Wrong input", "Minimum coefficient is 0.01");
            } else
                Lighting.k = Float.parseFloat(lightingCoeffTextField.getText());
        } catch (Exception e) {
            showNumberAlertTextField();
        }
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
        try {
            camerasController.currentCamera.setTarget(new Vector3f(Float.parseFloat(directionX.getText()), Float.parseFloat(directionY.getText()), Float.parseFloat(directionZ.getText())));
            camerasController.currentCamera.setDistance(Float.parseFloat(distance.getText()));
            camerasController.currentCamera.setRotation(new Vector2f(Float.parseFloat(cameraX.getText()) / 180 * Linal.pi, Float.parseFloat(cameraY.getText()) / 180 * Linal.pi));
        } catch (Exception e) {
            showNumberAlertTextField();
        }
    }

    @FXML
    private void handleCameraFieldUpdate(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            updateCameraDirectionAndPosition();
        }
    }

    // Меню моделей
    @FXML
    private void handleCoefChange(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLightingCoefChange();
        }
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
            root.getChildren().add(new TreeItem<>(modelController.getModelList().get(i).getName()));
        }
        objectsTree.setShowRoot(false);
        setCurrentModel(root.getChildren().size() - 1);
        updateChoiceBoxes();
        clearLabelsAndNullifySelectedFrame();
        handleAnimationsTree();
    }

    @FXML
    private void removeModel() {
        if (objectsTree.getSelectionModel().getSelectedIndex() < 0) {
            showWarning("No model to remove", "Empty sheet, nothing to remove");
            return;
        }
        animationController.animations.remove(modelController.currentModel);
        modelController.removeModel(objectsTree.getSelectionModel().getSelectedIndex());
        updateModelTree();
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
        handleAnimationsTree();
        clearLabelsAndNullifySelectedFrame();
        updateModelTransformation();

    }

    private void handleAnimationsTree() {
        if (modelController.currentModel != null)
            updateAnimationTree();
        else
            clearTree();
    }

    private void updateChoiceBoxes() {
        try {
//            choiceBoxRenderMode.setValue(modelController.currentModel.getCurrentModeCode());
            colorPicker.setValue(modelController.currentModel.getCurrentColorCode());
            switch (modelController.currentModel.getCurrentModeCode()) {
                case "Grid":
                    handleCheckboxSelection(true, false,false,false);
                    handleSetDisabledCheckboxes(false, false, true);
                    break;
                case "GridColor":
                    handleCheckboxSelection(true, true, false, false);
                    handleSetDisabledCheckboxes(false, true, false);
                    break;
                case "GridColorLight":
                    handleCheckboxSelection(true, true, false, true);
                    handleSetDisabledCheckboxes(false, true, false);
                    break;
                case "GridTexture":
                    handleCheckboxSelection(true, false, true, false);
                    handleSetDisabledCheckboxes(true, false, false);
                    break;
                case "GridTextureLight":
                    handleCheckboxSelection(true, false, true, true);
                    handleSetDisabledCheckboxes(true, false, false);
                    break;
                case "ColorLight":
                    handleCheckboxSelection(false, true, false, true);
                    handleSetDisabledCheckboxes(false, true, false);
                    break;
                case "Texture":
                    handleCheckboxSelection(false, false, true, false);
                    handleSetDisabledCheckboxes(true, false, false);
                    break;
                case "TextureLight":
                    handleCheckboxSelection(false, false, true, true);
                    handleSetDisabledCheckboxes(true, false, false);
                    break;
                default:
                    handleCheckboxSelection(false, false, false, false);
                    handleSetDisabledCheckboxes(false, false, true);
                    break;
            }
        } catch (Exception ignored) {}
    }

    @FXML
    private void handleRenderChoiceBoxChoice() {
        if (modelController.currentModel != null) {
            if ((!colorCheckbox.isSelected() && !textureCheckbox.isSelected()) && lightCheckbox.isSelected()) {
                lightCheckbox.setSelected(false);
                lightCheckbox.setDisable(true);
            }
            modelController.currentModel.setRenderableFlag(true);
            if (gridCheckbox.isSelected() && !colorCheckbox.isSelected() && !textureCheckbox.isSelected() && !lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderMode(RenderModeFactory.grid());
                modelController.currentModel.setCurrentModeCode("Grid");
                handleSetDisabledCheckboxes(false, false, true);
            } else if (gridCheckbox.isSelected() && colorCheckbox.isSelected() && !textureCheckbox.isSelected() && !lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderMode(RenderModeFactory.gridPlainColor(convertColor(modelController.currentModel.getCurrentColorCode())));
                modelController.currentModel.setCurrentModeCode("GridColor");
                handleSetDisabledCheckboxes(false, true, false);
            } else if (gridCheckbox.isSelected() && colorCheckbox.isSelected() && !textureCheckbox.isSelected() && lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderMode(RenderModeFactory.gridPlainColorLighting(convertColor(modelController.currentModel.getCurrentColorCode())));
                modelController.currentModel.setCurrentModeCode("GridColorLight");
                handleSetDisabledCheckboxes(false, true, false);
            } else if (gridCheckbox.isSelected() && !colorCheckbox.isSelected() && textureCheckbox.isSelected() && !lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderMode(RenderModeFactory.gridTexture(modelController.currentModel.getTexture()));
                modelController.currentModel.setCurrentModeCode("GridTexture");
                handleSetDisabledCheckboxes(true, false, false);
            } else if (gridCheckbox.isSelected() && !colorCheckbox.isSelected() && textureCheckbox.isSelected() && lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderMode(RenderModeFactory.gridTextureLighting(modelController.currentModel.getTexture()));
                modelController.currentModel.setCurrentModeCode("GridTextureLight");
                handleSetDisabledCheckboxes(true, false, false);
            } else if (!gridCheckbox.isSelected() && colorCheckbox.isSelected() && !textureCheckbox.isSelected() && lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderMode(RenderModeFactory.plainColorLighting(convertColor(modelController.currentModel.getCurrentColorCode())));
                modelController.currentModel.setCurrentModeCode("ColorLight");
                handleSetDisabledCheckboxes(false, true, false);
            } else if (!gridCheckbox.isSelected() && !colorCheckbox.isSelected() && textureCheckbox.isSelected() && !lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderMode(RenderModeFactory.texture(modelController.currentModel.getTexture()));
                modelController.currentModel.setCurrentModeCode("Texture");
                handleSetDisabledCheckboxes(true, false, false);
            } else if (!gridCheckbox.isSelected() && colorCheckbox.isSelected() && !textureCheckbox.isSelected() && !lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderMode(RenderModeFactory.plainColor(convertColor(modelController.currentModel.getCurrentColorCode())));
                modelController.currentModel.setCurrentModeCode("Color");
                handleSetDisabledCheckboxes(false, true, false);
            } else if (!gridCheckbox.isSelected() && !colorCheckbox.isSelected() && textureCheckbox.isSelected() && lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderMode(RenderModeFactory.textureLighting(modelController.currentModel.getTexture()));
                modelController.currentModel.setCurrentModeCode("TextureLight");
                handleSetDisabledCheckboxes(true, false, false);
            } else if (!gridCheckbox.isSelected() && colorCheckbox.isSelected() && !textureCheckbox.isSelected() && !lightCheckbox.isSelected()) {
                modelController.currentModel.setRenderableFlag(false);
                handleSetDisabledCheckboxes(false, true, false);
            } else {
                modelController.currentModel.setRenderableFlag(false);
                handleSetDisabledCheckboxes(false, false, true);
            }
        }
    }

    private void handleCheckboxSelection(boolean gridCheck, boolean colorCheck, boolean textureCheck, boolean lightCheck) {
        gridCheckbox.setSelected(gridCheck);
        colorCheckbox.setSelected(colorCheck);
        textureCheckbox.setSelected(textureCheck);
        lightCheckbox.setSelected(lightCheck);
    }

    private void handleSetDisabledCheckboxes(boolean colorCheck, boolean textureCheck, boolean lightCheck) {
        colorCheckbox.setDisable(colorCheck);
        textureCheckbox.setDisable(textureCheck);
        lightCheckbox.setDisable(lightCheck);
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
    private void handleColorPicker() {
        if (modelController.currentModel.model == null)
            return;
        modelController.currentModel.setCurrentColorCode(colorPicker.getValue());
        handleRenderChoiceBoxChoice();
    }

    private List<Integer> readNumbersFromTextField(TextField textField) {
        List<Integer> verticesToDelete = new ArrayList<>();
        String verticesInput = textField.getText().replace(',', ' ');
        Scanner scanner = new Scanner(verticesInput);
        while (scanner.hasNext()) {
            verticesToDelete.add(scanner.nextInt() - 1);
        }
        return verticesToDelete;
    }

    private List<Integer> readVerticesToDelete() {
        return readNumbersFromTextField(verticesToDeleteTextField);
    }

    private List<Integer> readPolygonsToDelete() {
        return readNumbersFromTextField(polygonsToDeleteTextField);
    }

    @FXML
    private void handleVertexRemover() {
        List<Integer> verticesToDelete;
        try {
            verticesToDelete = readVerticesToDelete();
        } catch (Exception e) {
            showNumberAlertTextField();
            return;
        }
        try {
            VertexRemoverNextGen.processModelAndCleanEverything(modelController.currentModel.model, verticesToDelete);
            modelController.currentModel.model.normals = Linal.calculateVerticesNormals(modelController.currentModel.model.vertices, modelController.currentModel.model.polygons);
            for (Polygon polygon : modelController.currentModel.model.polygons)
                Triangulation.convexPolygonTriangulate(polygon);
        } catch (NullPointerException npe) {
            showError("No model selected", "No model found, check everything and try again");
        }
    }

    @FXML
    private void handleVertexRemoverField(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleVertexRemover();
        }
    }

    @FXML
    private void handlePolygonRemoverField(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handlePolygonsRemover();
        }
    }

    @FXML
    private void handlePolygonsRemover() {
        List<Integer> polygonsToDelete;
        try {
            polygonsToDelete = readPolygonsToDelete();
        } catch (Exception e) {
            showNumberAlertTextField();
            return;
        }
        try {
            PolygonRemover.processModelAndCleanPolygons(modelController.currentModel.model, polygonsToDelete, true, true, true);
            modelController.currentModel.model.normals = Linal.calculateVerticesNormals(modelController.currentModel.model.vertices, modelController.currentModel.model.polygons);
            for (Polygon polygon : modelController.currentModel.model.polygons)
                Triangulation.convexPolygonTriangulate(polygon);
        } catch (NullPointerException npe) {
            showError("No model selected", "No model found, check everything and try again");
        }
    }

    @FXML
    private File readPolygonsVerticesFromFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt (*.txt)", "*.txt"));
        fileChooser.setTitle("Load Vertices");

        return fileChooser.showOpenDialog((Stage) imageView.getScene().getWindow());
    }

    @FXML
    private void handleVerticesFromTxt() {
        File verticesFile = readPolygonsVerticesFromFileChooser();
        if (verticesFile == null) {
            return;
        }
        try {
            readVerticesFromFile(verticesFile);
        } catch (Exception e) {
            showError("Error", "Make sure ur file consists of vertex numbers separated by space");
        }
    }

    @FXML
    private void handlePolygonsFromTxt() {
        File polygonsFile = readPolygonsVerticesFromFileChooser();
        if (polygonsFile == null) {
            return;
        }
        try {
            readPolygonsFromFile(polygonsFile);
        } catch (Exception e) {
            showError("Error", "Make sure ur file consists of polygon numbers separated by space");
        }
    }


    @FXML
    public void readPolygonsFromFile(File file) throws IOException {
        List<Integer> readPolygons = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileReader(file))) {
            while (scanner.hasNext()) {
                readPolygons.add(scanner.nextInt());
            }
        }
        polygonsToDeleteTextField.setText(readPolygons.toString().substring(1, readPolygons.toString().length() - 1));
    }

    @FXML
    public void readVerticesFromFile(File file) throws IOException {
        List<Integer> readVertices = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileReader(file))) {
            while (scanner.hasNext()) {
                readVertices.add(scanner.nextInt());
            }
        }
        verticesToDeleteTextField.setText(readVertices.toString().substring(1, readVertices.toString().length() - 1));
    }

    private void updateModelTransformation() {
        if (modelController.currentModel != null) {
            scaleX.setText(String.valueOf(modelController.currentModel.model.scale.x()));
            scaleY.setText(String.valueOf(modelController.currentModel.model.scale.y()));
            scaleZ.setText(String.valueOf(modelController.currentModel.model.scale.z()));

            rotationX.setText(String.valueOf(modelController.currentModel.model.rotation.x() * 180 / Linal.pi));
            rotationY.setText(String.valueOf(modelController.currentModel.model.rotation.y() * 180 / Linal.pi));
            rotationZ.setText(String.valueOf(modelController.currentModel.model.rotation.z() * 180 / Linal.pi));

            positionX.setText(String.valueOf(modelController.currentModel.model.position.x()));
            positionY.setText(String.valueOf(modelController.currentModel.model.position.y()));
            positionZ.setText(String.valueOf(modelController.currentModel.model.position.z()));
        } else {
            scaleX.setText("1");
            scaleY.setText("1");
            scaleZ.setText("1");
            rotationX.setText("0");
            rotationY.setText("0");
            rotationZ.setText("0");
            positionX.setText("0");
            positionY.setText("0");
            positionZ.setText("0");
        }
    }

    @FXML
    private void handleModelTransformation() {
        playAnimationFlag = false;
        try {
            modelController.currentModel.model.scale = new Vector3f(Float.parseFloat(scaleX.getText()), Float.parseFloat(scaleY.getText()), Float.parseFloat(scaleZ.getText()));
            modelController.currentModel.model.rotation = new Vector3f(Float.parseFloat(rotationX.getText()) / 180 * Linal.pi, Float.parseFloat(rotationY.getText()) / 180 * Linal.pi, Float.parseFloat(rotationZ.getText()) / 180 * Linal.pi);
            modelController.currentModel.model.position = new Vector3f(Float.parseFloat(positionX.getText()), Float.parseFloat(positionY.getText()), Float.parseFloat(positionZ.getText()));
        } catch (Exception e) {
            showNumberAlertTextField();
        }
    }

    @FXML
    private void handleModelTransformationField(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleModelTransformation();
        }
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorRunLater(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void showNumberAlertTextField() {
        showError("Wrong input", "Use float numbers for each field");
    }

    @FXML
    private void musicChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Song (*.wav)", "*.wav"));
        fileChooser.setTitle("Load Music");

        File file = fileChooser.showOpenDialog((Stage) imageView.getScene().getWindow());
        if (file == null) {
            return;
        }

        service.submit(new Runnable() {
            public void run() {
                playMusic(file);
            }
        });
    }

    private void playMusic(File file) {
        try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

        } catch (FileNotFoundException e) {
            showError("Music", "File not found");
        } catch (UnsupportedAudioFileException e) {
            showError("Music", "File is not supported");
        } catch (IOException | LineUnavailableException e) {
            showError("Sth", "Bruh, sth went wrong");
        }
    }

    @FXML
    private void handleNewFrame () {
        try {
            Model currModel = modelController.currentModel.model;
            animationController.animations.get(modelController.currentModel).addFrame(new Frame(
                            new State(currModel.position, currModel.rotation, currModel.scale),
                            new State(currModel.position, currModel.rotation, currModel.scale),
                            0
                    )
            );
            updateAnimationTree();
            animationController.selectedFrame = animationController.animations.get(modelController.currentModel).getFrames().get(framesTree.getRoot().getChildren().size() - 1);
            framesTree.getSelectionModel().select(framesTree.getRoot().getChildren().size() - 1);
            updateAnimationInformation();
        } catch (NullPointerException npe) {
            showError("No model", "No model selected, aborted");
        }
    }

    private void updateAnimationTree() {
        TreeItem<String> root = new TreeItem<>("Frames");
        framesTree.setRoot(root);
        for (int i = 0; i < animationController.animations.get(modelController.currentModel).getFrames().size(); i++) {
            root.getChildren().add(new TreeItem<>("Frame " + (i + 1)));
        }
        framesTree.setShowRoot(false);
    }

    private void clearTree() {
        TreeItem<String> root = new TreeItem<>("Frames");
        framesTree.setRoot(root);
        framesTree.setShowRoot(false);
    }

    @FXML
    private void handleFrameSelection(MouseEvent event) {
        try {
            TreeItem<String> selectedItem = framesTree.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int selectedIndex = framesTree.getRoot().getChildren().indexOf(selectedItem);
                if (selectedIndex >= 0) {
                    animationController.selectedFrame = animationController.animations.get(modelController.currentModel).getFrames().get(selectedIndex);
                    framesTree.getSelectionModel().select(selectedIndex);
                }
            }
            updateAnimationInformation();
        } catch (NullPointerException ignored) {
            // Ошибка от клика по пустому treeview. Говорить юзеру не о чем, нужно просто игнорировать
        }
    }

    @FXML
    private void handleSetInitialState() {
        try {
            ModelPrepared currModel = modelController.currentModel;
            animationController.selectedFrame.setInitialState(new State(currModel.model.position, currModel.model.rotation, currModel.model.scale));
            updateAnimationInformation();
        } catch (NullPointerException npe) {
            showError("No frame", "No frame selected, aborted");
        }
    }

    @FXML
    private void handleSetDestinationState() {
        try {
            Model currModel = modelController.currentModel.model;
            animationController.selectedFrame.setDestinationState(new State(currModel.position, currModel.rotation, currModel.scale));
            updateAnimationInformation();
        } catch (NullPointerException npe) {
            showError("No frame", "No frame selected, aborted");
        }
    }

    @FXML
    private void handleSetDuration() {
        try {
            animationController.selectedFrame.setDuration((long) (Float.parseFloat(frameDuration.getText()) * 1000) > 0 ? (long) (Float.parseFloat(frameDuration.getText()) * 1000) : 0);
            updateAnimationInformation();
        } catch (NullPointerException npe) {
            showError("No frame", "No frame selected");
        } catch (NumberFormatException nfe) {
            showNumberAlertTextField();
        }
    }

    @FXML
    private void handleSetDurationWithEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleSetDuration();
        }
    }

    @FXML
    private void handlePlayAnimationFLag() {
        playAnimationFlag = !playAnimationFlag;
    }

    private void updateAnimationInformation() {
        if (animationController.animations.get(modelController.currentModel).getFrames().isEmpty()) {
            clearLabelsAndNullifySelectedFrame();
            return;
        }
        durationLabel.setText(animationController.selectedFrame.getDuration() / 1000.0 + " s");
        frameDuration.setText(String.valueOf(animationController.selectedFrame.getDuration() / 1000.0));
        State destinationState = animationController.selectedFrame.getDestinationState();
        State initialState = animationController.selectedFrame.getInitialState();
        destinationVectors.setText(String.format(("(%.2f, %.2f, %.2f)\n(%.2f, %.2f, %.2f)\n(%.2f, %.2f, %.2f)"),
                destinationState.getScale().x(), destinationState.getScale().y(), destinationState.getScale().z(),
                destinationState.getRotation().x() / Linal.pi * 180, destinationState.getRotation().y() / Linal.pi * 180, destinationState.getRotation().z() / Linal.pi * 180,
                destinationState.getPosition().x(), destinationState.getPosition().y(), destinationState.getPosition().z()
                ));
        initialVectors.setText(String.format(("(%.2f, %.2f, %.2f)\n(%.2f, %.2f, %.2f)\n(%.2f, %.2f, %.2f)"),
                initialState.getScale().x(), initialState.getScale().y(), initialState.getScale().z(),
                initialState.getRotation().x() / Linal.pi * 180, initialState.getRotation().y() / Linal.pi * 180, initialState.getRotation().z() / Linal.pi * 180,
                initialState.getPosition().x(), initialState.getPosition().y(), initialState.getPosition().z()
        ));
    }

    @FXML
    private void removeFrame() {
        if (animationController.animations.get(modelController.currentModel).getFrames().isEmpty()) {
            showError("Nothing to remove here", "Nothing to remove here, aborted");
        }
        TreeItem<String> selectedItem = framesTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = framesTree.getRoot().getChildren().indexOf(selectedItem);
            if (selectedIndex >= 0) {
                animationController.animations.get(modelController.currentModel).removeFrame(selectedIndex);
            } else {
                showWarning("Nothing to remove", "Nothing to remove here, no frames left");
            }
        }
        updateAnimationTree();
        framesTree.getSelectionModel().select(framesTree.getRoot().getChildren().size() - 1);
        if (!framesTree.getRoot().getChildren().isEmpty()) {
            animationController.selectedFrame = animationController.animations.get(modelController.currentModel).getFrames().get(framesTree.getRoot().getChildren().size() - 1);
        }
        updateAnimationInformation();
    }

    @FXML
    private void loadAnimation() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Animation (*.json)", "*.json"));
        fileChooser.setTitle("Load texture");

        File file = fileChooser.showOpenDialog((Stage) imageView.getScene().getWindow());
        if (file == null) {
            return;
        }

        Map<String, ModelPrepared> models = modelController.getModelList().stream().collect(Collectors.toMap(
                ModelPrepared::getName,
                m -> m
        ));
        Map<ModelPrepared, ModelAnimation> loadedAnimations = AnimationReader.readAnimations(models, file.toString());

        for (ModelPrepared modelPrepared : loadedAnimations.keySet()) {
            if (!modelController.getModelList().contains(modelPrepared)) {
                showError("Wrong animation", "Model list doesnt match the animation. Check and try again");
                return;
            }
        }

        animationController.animations = loadedAnimations;
        updateAnimationTree();
        clearLabelsAndNullifySelectedFrame();
    }

    @FXML
    private void saveAnimation() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("Animation");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Animation (*.json)", "*.json"));
        fileChooser.setTitle("Save animation");

        File file = fileChooser.showSaveDialog((Stage) imageView.getScene().getWindow());

        if (file == null) {
            return;
        }

        String filename = file.getAbsolutePath();

        try {
            AnimationWriter.writeAnimations(animationController.animations, filename);
        } catch (Exception e) {
            showError("Error", "Error while writing the file");
        }
    }

    private void clearLabelsAndNullifySelectedFrame() {
        animationController.selectedFrame = null;
        durationLabel.setText("");
        initialVectors.setText("");
        destinationVectors.setText("");
    }
    @FXML
    private void handleLoopCheckbox() {
        loopFlag = !loopFlag;
    }

    @FXML
    private void handleAnimationReset() {
        animationController.reset();
        updateModelTransformation();
    }

    @FXML
    private void musicPauseResume() {
        try {
            if (musicPlaying) {
                clip.stop();
            } else {
                if (musicLoop.isSelected()) {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                } else
                    clip.start();
            }
            musicPlaying = !musicPlaying;
        } catch (Exception e) {
            showError("Music not found", "Check the file u've loaded");
        }
    }
    @FXML
    private void handleMusicLoopCheck() {
        try {
            if (musicLoop.isSelected() && musicPlaying) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else if (!musicLoop.isSelected() && musicPlaying) {
                clip.loop(0);
                clip.start();
            } else {
                clip.stop();
            }
        } catch (NullPointerException ignored) {
            // ничего не надо делать, юзер просто музыку еще не подгрузил.
        }
    }
    @FXML
    private void musicReset() {
        try {
            clip.setFramePosition(0);
        } catch (Exception e) {
            showError("Music not found", "Check the file u've loaded");
        }
    }

    public void shutdownExecutorService() {
        if (service != null && !service.isShutdown()) {
            service.shutdown();
            try {
                if (!service.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    service.shutdownNow();
                }
            } catch (InterruptedException e) {
                service.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}