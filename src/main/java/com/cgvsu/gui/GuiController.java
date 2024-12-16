package com.cgvsu.gui;

import com.cgvsu.io.objwriter.ObjWriter;
import com.cgvsu.math.Linal;
import com.cgvsu.model.ModelPrepared;
import com.cgvsu.model.Polygon;
import com.cgvsu.model_modification.PolygonRemover;
import com.cgvsu.model_modification.VertexRemoverNextGen;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.render_engine.*;
import com.cgvsu.triangulation.Triangulation;
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
import java.io.FileReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.cgvsu.model.Model;
import com.cgvsu.io.objreader.ObjReader;

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

        RenderEngine renderEngine = new RenderEngine();

        initializeCamerasController();

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
    private void switchThemeToDark() {
        Scene scene = anchorPane.getScene();

        if (scene == null) {
            return;
        }

        anchorPane.getStylesheets().clear();

        File style = new File("src/main/resources/styles/darker-theme.css");
        try {
            scene.getStylesheets().add(style.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            showAlert("Sth wrong", "idk");
        }
    }

    @FXML
    private void switchThemeToLight() {
        Scene scene = anchorPane.getScene();
        if (scene == null) {
            return;
        }

        scene.getStylesheets().clear();

        File style = new File("src/main/resources/styles/lighter-theme.css");
        try {
            scene.getStylesheets().add(style.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            showAlert("Sth wrong", "idk");
        }
    }

    private void initializeCamerasController() {
        if (camerasController.getCamerasQuantity() == 1) {
            updateCameraTree();
            setCurrentCamera(0);
            System.out.println("lol");
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
        List<Integer> verticesToDelete = readVerticesToDelete();
        VertexRemoverNextGen.processModelAndCleanEverything(modelController.currentModel.model, verticesToDelete);
        modelController.currentModel.model.normals = Linal.calculateVerticesNormals(modelController.currentModel.model.vertices, modelController.currentModel.model.polygons);
        for (Polygon polygon : modelController.currentModel.model.polygons)
            Triangulation.convexPolygonTriangulate(polygon);
    }

    @FXML
    private void handlePolygonsRemover() {
        List<Integer> polygonsToDelete = readPolygonsToDelete();
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
            showAlert(e.getLocalizedMessage(), e.getMessage());
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
            showAlert(e.getLocalizedMessage(), e.getMessage());
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
            showAlert(e.getLocalizedMessage(), e.getMessage());
        }
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

        camerasController.currentCamera.moveRotation(new Vector2f(deltax / 100, -deltay / 100));
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
    private void handleGraphicConveyor() {
        modelController.currentModel.model.scale = new Vector3f(Float.parseFloat(scaleX.getText()), Float.parseFloat(scaleY.getText()), Float.parseFloat(scaleZ.getText()));
        modelController.currentModel.model.rotation = new Vector3f(Float.parseFloat(rotationX.getText()), Float.parseFloat(rotationY.getText()), Float.parseFloat(rotationZ.getText()));
        modelController.currentModel.model.position = new Vector3f(Float.parseFloat(positionX.getText()), Float.parseFloat(positionY.getText()), Float.parseFloat(positionZ.getText()));
        modelController.currentModel.model.getModelMatrix();
    }

    @FXML
    private void handleNormalsMenuItem() {
        modelController.currentModel.model.normals = Linal.calculateVerticesNormals(modelController.currentModel.model.vertices, modelController.currentModel.model.polygons);
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

        if (modelController.currentModel.model == null)
            return;
        modelController.currentModel.setCurrentColorCode(colorPicker.getValue());
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