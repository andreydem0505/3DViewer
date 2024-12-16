package com.cgvsu.model_modification;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.*;

public class PolygonRemover {
    public static void processModelAndCleanPolygons(
            Model model,
            List<Integer> polygonsToDelete,
            boolean cleanUnusedVertices,
            boolean cleanUnusedTextureVertices,
            boolean cleanUnusedNormals
    ) {
        // Шаг 1: Удаляем указанные полигоны
        List<Polygon> polygons = model.getPolygons();
        ArrayList<Polygon> newPolygons = new ArrayList<>();

        Set<Integer> polygonsToDeleteSet = new HashSet<>(polygonsToDelete);
        for (int i = 0; i < polygons.size(); i++) {
            if (!polygonsToDeleteSet.contains(i)) {
                newPolygons.add(polygons.get(i)); // Добавляем только не удаленные полигоны
            }
        }

        // Шаг 2: Если флаг установлен, удаляем неиспользуемые вершины
        if (cleanUnusedVertices) {
            cleanUnusedElements(
                    model.getVertices(),
                    newPolygons,
                    0 // Удаление вершин
            );
        }

        // Шаг 3: Если флаг установлен, удаляем неиспользуемые текстурные вершины
        if (cleanUnusedTextureVertices) {
            cleanUnusedElements(
                    model.getTextureVertices(),
                    newPolygons,
                    1 // Удаление текстурных координат
            );
        }

        // Шаг 4: Если флаг установлен, удаляем неиспользуемые нормали
        if (cleanUnusedNormals) {
            cleanUnusedElements(
                    model.getNormals(),
                    newPolygons,
                    2 // Удаление нормалей
            );
        }

        // Устанавливаем обновленный список полигонов в модель
        model.setPolygons(newPolygons);
    }

    private static <T> void cleanUnusedElements(
            List<T> elements,
            List<Polygon> polygons,
            int componentIndex
    ) {
        // Шаг 1: Определяем, какие элементы используются
        Set<Integer> usedIndices = new HashSet<>();
        for (Polygon polygon : polygons) {
            List<Integer> indices;
            switch (componentIndex) {
                case 0: // Вершины
                    indices = polygon.getVertexIndices();
                    break;
                case 1: // Текстурные вершины
                    indices = polygon.getTextureVertexIndices();
                    break;
                case 2: // Нормали
                    indices = polygon.getNormalIndices();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid component index");
            }
            usedIndices.addAll(indices);
        }

        // Шаг 2: Создаем новую нумерацию используемых элементов
        Map<Integer, Integer> newIndexMapping = new HashMap<>();
        List<T> newElements = new ArrayList<>();
        int newIndex = 0;

        for (int i = 0; i < elements.size(); i++) {
            if (usedIndices.contains(i)) {
                newIndexMapping.put(i, newIndex); // Сопоставляем старый индекс с новым
                newElements.add(elements.get(i));
                newIndex++;
            }
        }

        // Шаг 3: Обновляем индексы элементов в полигонах
        for (Polygon polygon : polygons) {
            List<Integer> indices;
            switch (componentIndex) {
                case 0: // Вершины
                    indices = polygon.getVertexIndices();
                    break;
                case 1: // Текстурные вершины
                    indices = polygon.getTextureVertexIndices();
                    break;
                case 2: // Нормали
                    indices = polygon.getNormalIndices();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid component index");
            }

            ArrayList<Integer> updatedIndices = new ArrayList<>();
            for (int oldIndex : indices) {
                updatedIndices.add(newIndexMapping.get(oldIndex));
            }

            // Применяем обновленные индексы к полигону
            switch (componentIndex) {
                case 0:
                    polygon.setVertexIndices(updatedIndices);
                    break;
                case 1:
                    polygon.setTextureVertexIndices(updatedIndices);
                    break;
                case 2:
                    polygon.setNormalIndices(updatedIndices);
                    break;
            }
        }

        // Устанавливаем обновленный список элементов
        elements.clear();
        elements.addAll(newElements);
    }
}
