package com.cgvsu.modelModification;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class VertexRemoverNextGen {
    public static void processModelAndCleanAllUnusedElements(Model model, List<Integer> verticesToDelete, boolean cleanUpUnusedNormals, boolean cleanUpUnusedTextures) {
        processModel(model, verticesToDelete, false, true, cleanUpUnusedNormals, cleanUpUnusedTextures, true);
    }

    public static void processModelCleanPolygonsAndObsoleteNormals(Model model, List<Integer> verticesToDelete) {
        processModel(model, verticesToDelete, false, true, true, false, false);
    }

    public static void processModelCleanPolygonsAndObsoleteTextures(Model model, List<Integer> verticesToDelete) {
        processModel(model, verticesToDelete, false, true, false, true, false);
    }

    public static void processModelAndDoNothing(Model model, List<Integer> verticesToDelete) {
        processModel(model, verticesToDelete, true, false, false, false, false);
    }

    public static void processModelAndCleanEverything(Model model, List<Integer> verticesToDelete) {
        processModel(model, verticesToDelete, false, true, true, true, true);
    }

    public static void processModel(Model model, List<Integer> verticesToDelete, boolean keepHangingFaces, boolean cleanHangingPolygonsAfterwards, boolean cleanUpUnusedNormals, boolean cleanUpUnusedTextures, boolean cleanAllUnused) {
        List<Vector3f> vertices = model.getVertices();
        ArrayList<Vector2f> textureVertices = model.getTextureVertices();
        ArrayList<Vector3f> normals = model.getNormals();
        List<Polygon> faces = model.getPolygons();

        Map<Integer, Integer> vertexIndexMapping = new HashMap<>();

        ArrayList<Vector3f> newVertices = removeVertices(vertices, verticesToDelete, vertexIndexMapping);
        ArrayList<Polygon> newFaces = updateFaces(faces, verticesToDelete, vertexIndexMapping, keepHangingFaces);

        // Отдельно, чтобы было 3 случая: мы оставляем все висячие, мы оставляем только старые висячие, мы не оставляем висячих.
        if (cleanHangingPolygonsAfterwards) {
            Set<Integer> validVertexIndices = collectValidIndices(vertices);
            Set<Integer> validTextureIndices = collectValidIndices(textureVertices);
            Set<Integer> validNormalIndices = collectValidIndices(normals);
            newFaces = cleanFaces(newFaces, validVertexIndices, validTextureIndices, validNormalIndices);
        }

        if (cleanUpUnusedTextures) {
            Set<Integer> initiallyUsedTextureIndices = collectInitiallyUsedIndices(faces, 1);
            Map<Integer, Integer> textureIndexMapping = new HashMap<>();
            if (cleanAllUnused)
                removeAllUnusedData(textureVertices, textureIndexMapping, newFaces, 1);
            else
                removeObsoleteData(textureVertices, textureIndexMapping, newFaces, 1, initiallyUsedTextureIndices);
        }

        if (cleanUpUnusedNormals) {
            Set<Integer> initiallyUsedNormalIndices = collectInitiallyUsedIndices(faces, 2);
            Map<Integer, Integer> normalIndexMapping = new HashMap<>();
            if (cleanAllUnused)
                removeAllUnusedData(normals, normalIndexMapping, newFaces, 2);
            else
                removeObsoleteData(normals, normalIndexMapping, newFaces, 2, initiallyUsedNormalIndices);
        }

        model.setVertices(newVertices);
        model.setNormals(normals);
        model.setTextureVertices(textureVertices);
        model.setPolygons(newFaces);
//
//        vertexValidation(model);
    }

    public static void vertexValidation(Model model) {
        ArrayList<Vector3f> vertices = model.getVertices();
        ArrayList<Polygon> polygons = model.getPolygons();

        // Определяем, какие вершины используются
        Set<Integer> usedVertexIndices = new HashSet<>();
        for (Polygon polygon : polygons) {
            usedVertexIndices.addAll(polygon.getVertexIndices());
        }

        // Создаем новую нумерацию для используемых вершин
        Map<Integer, Integer> newIndexMapping = new HashMap<>();
        ArrayList<Vector3f> newVertices = new ArrayList<>();
        int newIndex = 0;

        for (int i = 0; i < vertices.size(); i++) {
            if (usedVertexIndices.contains(i)) {
                newIndexMapping.put(i, newIndex); // Сопоставляем старый индекс с новым
                newVertices.add(vertices.get(i)); // Добавляем вершину в новый список
                newIndex++;
            }
        }

        // Обновляем индексы вершин в полигонах
        ArrayList<Polygon> updatedPolygons = new ArrayList<>();
        for (Polygon polygon : polygons) {
            List<Integer> oldIndices = polygon.getVertexIndices();
            ArrayList<Integer> updatedIndices = new ArrayList<>();

            for (int oldIndex : oldIndices) {
                updatedIndices.add(newIndexMapping.get(oldIndex)); // Преобразуем старый индекс в новый
            }

            // Создаем новый полигон с обновленными индексами
            Polygon updatedPolygon = new Polygon();
            updatedPolygon.setVertexIndices(updatedIndices);
            updatedPolygon.setNormalIndices(polygon.getNormalIndices());
            updatedPolygon.setTextureVertexIndices(polygon.getTextureVertexIndices());
            updatedPolygons.add(updatedPolygon);
        }

       // Устанавливаем обновленные данные в модель
        model.setVertices(newVertices);
        model.setPolygons(updatedPolygons);
    }


    private static ArrayList<Vector3f> removeVertices(List<Vector3f> vertices, List<Integer> verticesToDelete, Map<Integer, Integer> indexMapping) {
        Set<Integer> verticesSet = new HashSet<>(verticesToDelete);
        ArrayList<Vector3f> newVertices = new ArrayList<>();
        int newIndex = 0;

        for (int i = 0; i < vertices.size(); i++) {
            if (!verticesSet.contains(i)) { // если ее не надо удалять, то мы добавляем в новые вершины.
                newVertices.add(vertices.get(i));
                indexMapping.put(i, newIndex);
                newIndex++;
            }
        }
        return newVertices;
    }

    private static ArrayList<Polygon> updateFaces(List<Polygon> faces, List<Integer> verticesToDelete, Map<Integer, Integer> vertexIndexMapping, boolean keepHangingFaces) {
        Set<Integer> verticesSet = new HashSet<>(verticesToDelete); // Удаляемые вершины
        ArrayList<Polygon> newFaces = new ArrayList<>();
        for (Polygon face : faces) {
            boolean containsDeletedVertex = false;
            Polygon newFace = new Polygon();

            List<Integer> faceVertices = face.getVertexIndices();
            List<Integer> faceTextures = face.getTextureVertexIndices();
            List<Integer> faceNormals = face.getNormalIndices();

            ArrayList<Integer> newFaceVertices = new ArrayList<>();
            ArrayList<Integer> newFaceTextures = new ArrayList<>();
            ArrayList<Integer> newFaceNormals = new ArrayList<>();

            for (int j = 0; j < faceVertices.size(); j++) {

                // Индекс вершины
                int vertexIndex = faceVertices.get(j);

                // Проверяем, удалена ли вершина
                if (verticesSet.contains(vertexIndex)) {
                    containsDeletedVertex = true;
                    if (!keepHangingFaces)
                        break; // Удаляем весь полигон
                    continue; // Пропускаем удалённую вершину
                }

                newFaceVertices.add(vertexIndexMapping.getOrDefault(vertexIndex, vertexIndex));   // Индекс вершины

                if (!faceTextures.isEmpty()) {
                    newFaceTextures.add(faceTextures.get(j)); // добавляем текстуру, если существует
                }

                if (!faceNormals.isEmpty()) {
                    newFaceNormals.add(faceNormals.get(j));  // Добавляем нормаль, если существует
                }

                newFace.setVertexIndices(newFaceVertices);
                newFace.setNormalIndices(newFaceNormals);
                newFace.setTextureVertexIndices(newFaceTextures);
            }
            // Добавляем полигон только если в нем остались вершины
            if (!newFaceVertices.isEmpty() && (!containsDeletedVertex || keepHangingFaces)) {
                newFaces.add(newFace);
            }
        }
        return newFaces;
    }

    private static Set<Integer> collectInitiallyUsedIndices(List<Polygon> faces, int componentIndex) {
        Set<Integer> usedIndices = new HashSet<>();
        for (Polygon face : faces) {
            ArrayList<Integer> faceVertices = face.getVertexIndices();
            for (int j = 0; j < faceVertices.size(); j++) {
                if (componentIndex == 1 && !face.getTextureVertexIndices().isEmpty()) {
                    usedIndices.add(face.getTextureVertexIndices().get(j));
                }
                else if (componentIndex == 2 && !face.getNormalIndices().isEmpty()) {
                    usedIndices.add(face.getNormalIndices().get(j));
                }
            }
        }
        return usedIndices;
    }

    private static <T> void removeObsoleteData(
            List<T> data,
            Map<Integer, Integer> indexMapping,
            List<Polygon> faces,
            int componentIndex,
            Set<Integer> initiallyUsedIndices
    ) {
        // Собираем текущие используемые индексы
        Set<Integer> currentlyUsedIndices = new HashSet<>();
        for (Polygon face : faces) {
            ArrayList<Integer> faceVertices = face.getVertexIndices();
            for (int j = 0; j < faceVertices.size(); j++) {
                if (componentIndex == 1 && !face.getTextureVertexIndices().isEmpty()) {
                    currentlyUsedIndices.add(face.getTextureVertexIndices().get(j));
                }
                else if (componentIndex == 2 && !face.getNormalIndices().isEmpty()) {
                    currentlyUsedIndices.add(face.getNormalIndices().get(j));
                }
            }
        }

        // Удаляем только те, которые стали неиспользуемыми
        List<T> newData = new ArrayList<>();
        int newIndex = 0;
        for (int i = 0; i < data.size(); i++) {
            if (currentlyUsedIndices.contains(i) || !initiallyUsedIndices.contains(i)) {
                newData.add(data.get(i));
                indexMapping.put(i, newIndex++);
            }
        }

        data.clear();
        data.addAll(newData);

        // Обновляем индексы в полигонах
        for (int i = 0; i < faces.size(); i++) {
            Polygon face = faces.get(i);
            Polygon newFace = new Polygon();

            List<Integer> faceVertices = face.getVertexIndices();
            List<Integer> faceTextures = face.getTextureVertexIndices();
            List<Integer> faceNormals = face.getNormalIndices();
            ArrayList<Integer> newFaceVertices = new ArrayList<>();
            ArrayList<Integer> newFaceTextures = new ArrayList<>();
            ArrayList<Integer> newFaceNormals = new ArrayList<>();

            for (int j = 0; j < faceVertices.size(); j++) {

                newFaceVertices.add(faceVertices.get(j));

                if (!faceTextures.isEmpty()) {
                    int textureIndex = faceTextures.get(j);
                    if (componentIndex == 1) {
                        newFaceTextures.add(indexMapping.getOrDefault(textureIndex, textureIndex));
                    } else {
                        newFaceTextures.add(textureIndex);
                    }
                }

                if (!faceNormals.isEmpty()) {
                    int normalIndex = faceNormals.get(j);
                    if (componentIndex == 2) {
                        newFaceNormals.add(indexMapping.getOrDefault(normalIndex, normalIndex));
                    } else {
                        newFaceNormals.add(normalIndex);
                    }
                }
            }

            newFace.setVertexIndices(newFaceVertices);
            newFace.setTextureVertexIndices(newFaceTextures);
            newFace.setNormalIndices(newFaceNormals);

            // Заменяем старую строку полигона на обновленную
            faces.set(i, newFace);
        }
    }


    private static ArrayList<Polygon> cleanFaces(ArrayList<Polygon> faces, Set<Integer> validVertexIndices, Set<Integer> validTextureIndices, Set<Integer> validNormalIndices) {
        ArrayList<Polygon> newFaces = new ArrayList<>();
        for (Polygon face : faces) {
            boolean isHangingFace = false;
            Polygon newFace = new Polygon();

            ArrayList<Integer> faceVertices = face.getVertexIndices();
            ArrayList<Integer> faceTextures = face.getTextureVertexIndices();
            ArrayList<Integer> faceNormals = face.getNormalIndices();

            ArrayList<Integer> newFaceVertices = new ArrayList<>();
            ArrayList<Integer> newFaceTextures = new ArrayList<>();
            ArrayList<Integer> newFaceNormals = new ArrayList<>();
            for (int j = 0; j < faceVertices.size(); j++) {

                int vertexIndex = faceVertices.get(j);
                if (!validVertexIndices.contains(vertexIndex)) {
                    isHangingFace = true;
                    break; // Если вершина невалидна, полигон висячий
                }

                // Проверяем текстурную координату
                if (!faceTextures.isEmpty()) {
                    int textureIndex = faceTextures.get(j);
                    if (!validTextureIndices.contains(textureIndex)) {
                        isHangingFace = true;
                        break;
                    }
                }

                // Проверяем нормаль
                if (!faceNormals.isEmpty()) {
                    int normalIndex = faceNormals.get(j);
                    if (!validNormalIndices.contains(normalIndex)) {
                        isHangingFace = true;
                        break;
                    }
                }
                // Формируем новую строку вершины
                newFaceVertices.add(faceVertices.get(j));
                if (!faceNormals.isEmpty())
                    newFaceTextures.add(faceTextures.get(j));
                if (!faceNormals.isEmpty())
                    newFaceNormals.add(faceNormals.get(j));
            }

            newFace.setVertexIndices(newFaceVertices);
            newFace.setTextureVertexIndices(newFaceTextures);
            newFace.setNormalIndices(newFaceNormals);

            // Добавляем только невисячие полигоны
            if (!newFaceVertices.isEmpty() && !isHangingFace) {
                newFaces.add(newFace);
            }
        }
        return newFaces;
    }

    private static Set<Integer> collectValidIndices(List<?> data) {
        Set<Integer> validIndices = new HashSet<>();
        for (int i = 0; i < data.size(); i++) {
            validIndices.add(i); // Существовавшие индексы
        }
        return validIndices;
    }

    private static <T> void removeAllUnusedData(List<T> data, Map<Integer, Integer> indexMapping, List<Polygon> faces, int componentIndex) { //Удаляем все текстурные вершины и нормали, которые не используются
        Set<Integer> currentlyUsedIndices = new HashSet<>();
        for (Polygon face : faces) {
            ArrayList<Integer> faceVertices = face.getVertexIndices();
            for (int j = 0; j < faceVertices.size(); j++) {
                if (componentIndex == 1 && !face.getTextureVertexIndices().isEmpty()) {
                    currentlyUsedIndices.add(face.getTextureVertexIndices().get(j));
                }
                else if (componentIndex == 2 && !face.getNormalIndices().isEmpty()) {
                    currentlyUsedIndices.add(face.getNormalIndices().get(j));
                }
            }
        }

        // Удаляем только те, которые стали неиспользуемыми
        List<T> newData = new ArrayList<>();
        int newIndex = 0;
        for (int i = 0; i < data.size(); i++) {
            if (currentlyUsedIndices.contains(i)) {
                newData.add(data.get(i));
                indexMapping.put(i, newIndex++);
            }
        }

        data.clear();
        data.addAll(newData);

        // Обновляем индексы в полигонах
        for (int i = 0; i < faces.size(); i++) {
            Polygon face = faces.get(i);
            Polygon newFace = new Polygon();

            List<Integer> faceVertices = face.getVertexIndices();
            List<Integer> faceTextures = face.getTextureVertexIndices();
            List<Integer> faceNormals = face.getNormalIndices();

            ArrayList<Integer> newFaceVertices = new ArrayList<>();
            ArrayList<Integer> newFaceTextures = new ArrayList<>();
            ArrayList<Integer> newFaceNormals = new ArrayList<>();

            for (int j = 0; j < faceVertices.size(); j++) {

                newFaceVertices.add(faceVertices.get(j));

                if (!faceTextures.isEmpty()) {
                    int textureIndex = faceTextures.get(j);
                    if (componentIndex == 1) {
                        newFaceTextures.add(indexMapping.getOrDefault(textureIndex, textureIndex));
                    } else {
                        newFaceTextures.add(textureIndex);
                    }
                }

                if (!faceNormals.isEmpty()) {
                    int normalIndex = faceNormals.get(j);
                    if (componentIndex == 2) {
                        newFaceNormals.add(indexMapping.getOrDefault(normalIndex, normalIndex));
                    } else {
                        newFaceNormals.add(normalIndex);
                    }
                }
            }

            newFace.setVertexIndices(newFaceVertices);
            newFace.setTextureVertexIndices(newFaceTextures);
            newFace.setNormalIndices(newFaceNormals);

            // Заменяем старую строку полигона на обновленную
            faces.set(i, newFace);
        }
    }
}
