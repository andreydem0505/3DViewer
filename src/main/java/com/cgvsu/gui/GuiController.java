package com.cgvsu.gui;

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
import com.cgvsu.rasterization.Lightning;
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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cgvsu.model.Model;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class GuiController {

    final private float TRANSLATION = 2F;

    @FXML
    private TreeView<String> camerasTree;

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
    private TextField lightningCoeffTextField;

    @FXML
    private TextField directionZ;

    @FXML
    private TextField cameraX;

    @FXML
    private TextField cameraY;

    @FXML
    private TextField distance;

    @FXML
    private ChoiceBox choiceBoxRenderMode;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private ImageView imageView;

    private volatile boolean musicPlaying = false;
    private ExecutorService service = Executors.newFixedThreadPool(4);
    private Clip clip;

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

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        imageView.setPickOnBounds(true); //schizophrenia, don't touch!
        imageView.setOnDragDetected(e -> {
            imageView.startFullDrag();
        });

        PixelWriter pixelWriter = new PixelWriter(imageView);

        RenderEngine renderEngine = new RenderEngine(pixelWriter);

        initializeCamerasController();

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = imageView.getFitWidth();
            double height = imageView.getFitHeight();

            pixelWriter.clearScreen();
            camerasController.currentCamera.setAspectRatio((float) (height / width));

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
    }

    private void initializeCamerasController() {
        if (camerasController.getCamerasQuantity() == 1) {
            updateCameraTree();
            setCurrentCamera(0);
        }
    }

    private void initializeFields() {
        choiceBoxRenderMode.getItems().addAll("Inactive", "Grid", "GridColor", "GridColorLight", "GridTexture", "GridTextureLight", "ColorLight", "Texture", "TextureLight");
        choiceBoxRenderMode.setValue("Grid");
        scaleX.setText("1");
        scaleY.setText("1");
        scaleZ.setText("1");
        rotationX.setText("0");
        rotationY.setText("0");
        rotationZ.setText("0");
        positionX.setText("0");
        positionY.setText("0");
        positionZ.setText("0");
        lightningCoeffTextField.setText(Float.toString(Lightning.k));
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
            if (modelController.getModelsQuantity() >= 1) {
                modelController.currentModel = modelController.getModelList().get(modelController.getModelsQuantity() - 1);
            }
        } catch (IOException exception) {
            showError("Error loading the model","Failed to load model.\nError: " + exception.getLocalizedMessage());
        }
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

    @FXML
    private void handleLightningCoefChange() {
        try {
            if (Float.parseFloat(lightningCoeffTextField.getText()) > 1) {
                Lightning.k = 1;
                lightningCoeffTextField.setText("1");
                showWarning("Wrong input", "Maximum coefficient is 1");
            } else if (Float.parseFloat(lightningCoeffTextField.getText()) <= 0.009) {
                Lightning.k = 0.01f;
                lightningCoeffTextField.setText("0.01");
                showWarning("Wrong input", "Minimum coefficient is 0.01");
            } else
                Lightning.k = Float.parseFloat(lightningCoeffTextField.getText());
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
            camerasController.currentCamera.setRotation(new Vector2f(Float.parseFloat(cameraX.getText()) / Linal.pi * 180, Float.parseFloat(cameraY.getText()) / Linal.pi * 180));
        } catch (Exception e) {
            showNumberAlertTextField();
        }
    }

    // Меню моделей
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
        setCurrentModel(root.getChildren().size() - 1);
        updateChoiceBoxes();
    }

    @FXML
    private void removeModel() {
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
    }

    private void updateChoiceBoxes() {
        try {
            choiceBoxRenderMode.setValue(modelController.currentModel.getCurrentModeCode());
            colorPicker.setValue(modelController.currentModel.getCurrentColorCode());
        } catch (Exception ignored) {}
    }

    @FXML
    private void handleRenderChoiceBoxChoice() {
        if (modelController.currentModel != null) {
            modelController.currentModel.setRenderableFlag(true);
            switch (choiceBoxRenderMode.getValue().toString()) {
                case "Grid":
                    modelController.currentModel.setRenderMode(RenderModeFactory.grid());
                    break;
                case "GridColor":
                    modelController.currentModel.setRenderMode(RenderModeFactory.gridPlainColor(convertColor(modelController.currentModel.getCurrentColorCode())));
                    break;
                case "GridColorLight":
                    modelController.currentModel.setRenderMode(RenderModeFactory.gridPlainColorLightning(convertColor(modelController.currentModel.getCurrentColorCode())));
                    break;
                case "GridTexture":
                    modelController.currentModel.setRenderMode(RenderModeFactory.gridTexture(modelController.currentModel.getTexture()));
                    break;
                case "GridTextureLight":
                    modelController.currentModel.setRenderMode(RenderModeFactory.gridTextureLightning(modelController.currentModel.getTexture()));
                    break;
                case "ColorLight":
                    modelController.currentModel.setRenderMode(RenderModeFactory.plainColorLightning(convertColor(modelController.currentModel.getCurrentColorCode())));
                    break;
                case "Texture":
                    modelController.currentModel.setRenderMode(RenderModeFactory.texture(modelController.currentModel.getTexture()));
                    break;
                case "TextureLight":
                    modelController.currentModel.setRenderMode(RenderModeFactory.textureLightning(modelController.currentModel.getTexture()));
                    break;
                default:
                    modelController.currentModel.setRenderableFlag(false);
                    break;
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
        VertexRemoverNextGen.processModelAndCleanEverything(modelController.currentModel.model, verticesToDelete);
        modelController.currentModel.model.normals = Linal.calculateVerticesNormals(modelController.currentModel.model.vertices, modelController.currentModel.model.polygons);
        for (Polygon polygon : modelController.currentModel.model.polygons)
            Triangulation.convexPolygonTriangulate(polygon);
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
        PolygonRemover.processModelAndCleanPolygons(modelController.currentModel.model, polygonsToDelete, true, true, true);
        modelController.currentModel.model.normals = Linal.calculateVerticesNormals(modelController.currentModel.model.vertices, modelController.currentModel.model.polygons);
        for (Polygon polygon : modelController.currentModel.model.polygons)
            Triangulation.convexPolygonTriangulate(polygon);
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

    @FXML
    private void handleModelTransformation() {
        try {
            modelController.currentModel.model.scale = new Vector3f(Float.parseFloat(scaleX.getText()), Float.parseFloat(scaleY.getText()), Float.parseFloat(scaleZ.getText()));
            modelController.currentModel.model.rotation = new Vector3f(Float.parseFloat(rotationX.getText()), Float.parseFloat(rotationY.getText()), Float.parseFloat(rotationZ.getText()));
            modelController.currentModel.model.position = new Vector3f(Float.parseFloat(positionX.getText()), Float.parseFloat(positionY.getText()), Float.parseFloat(positionZ.getText()));
        } catch (Exception e) {
            showNumberAlertTextField();
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

        musicPlaying = true;

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

            clip.start();
            while (musicPlaying) {
                Thread.onSpinWait();
            }

        } catch (FileNotFoundException e) {
            showError("Music", "File not found");
        }catch (UnsupportedAudioFileException e) {
            showError("Music", "File is not supported");
        } catch (IOException | LineUnavailableException e) {
            showError("Sth", "Bruh, sth went wrong");
        }
    }
    @FXML
    private void musicStop() {
        musicPlaying = false;
        clip.stop();
    }
}