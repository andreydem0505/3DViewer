package com.cgvsu.model_modification;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PolygonRemoverTest {
    @Test
    public void testRemoveSinglePolygonWithoutCleaning() {
        // Создаем модель с 4 вершинами и 2 полигонами
        Model model = new Model();
        model.setVertices(new ArrayList<>(List.of(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 1, 0)
        )));
        model.setTextureVertices(new ArrayList<>());
        model.setNormals(new ArrayList<>());
        Polygon polygon1 = new Polygon();
        Polygon polygon2 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon2.setVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        model.setPolygons(new ArrayList<>(List.of(polygon1, polygon2)));

        PolygonRemover.processModelAndCleanPolygons(model, Arrays.asList(1), true, false, false);

        // Проверяем, что остался только один полигон
        assertEquals(1, model.getPolygons().size());
        // Убедимся, что остались только нужные вершины (0, 1, 2)
        assertEquals(3, model.getVertices().size());
    }
    @Test
    public void testRemovePolygonAndKeepAllUnusedElements() {
        // Создаем модель с 4 вершинами и 2 полигонами
        Model model = new Model();
        model.setVertices(new ArrayList<>(List.of(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 1, 0)
        )));
        model.setTextureVertices(new ArrayList<>(List.of(
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(0, 1),
                new Vector2f(1, 1)
        )));
        model.setNormals(new ArrayList<>(List.of(
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 0, 0)
        )));

        Polygon polygon1 = new Polygon();
        Polygon polygon2 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon1.setTextureVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon1.setNormalIndices(new ArrayList<>(List.of(0, 1, 2)));

        polygon2.setVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        polygon2.setTextureVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        polygon2.setNormalIndices(new ArrayList<>(List.of(1, 2, 0)));

        model.setPolygons(new ArrayList<>(List.of(polygon1, polygon2)));

        // Удаляем второй полигон, но оставляем все неиспользуемые элементы
        PolygonRemover.processModelAndCleanPolygons(model, Arrays.asList(1), false, false, false);

        // Проверяем, что остался только один полигон
        assertEquals(1, model.getPolygons().size());
        // Проверяем, что все вершины остались
        assertEquals(4, model.getVertices().size());
        // Проверяем, что текстурные вершины остались
        assertEquals(4, model.getTextureVertices().size());
        // Проверяем, что нормали остались
        assertEquals(3, model.getNormals().size());
    }

    @Test
    public void testRemovePolygonAndCleanUnusedVertices() {
        // Создаем модель с 4 вершинами и 2 полигонами
        Model model = new Model();
        model.setVertices(new ArrayList<>(List.of(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 1, 0)
        )));
        model.setTextureVertices(new ArrayList<>());
        model.setNormals(new ArrayList<>());

        Polygon polygon1 = new Polygon();
        Polygon polygon2 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon2.setVertexIndices(new ArrayList<>(List.of(1, 2, 3)));

        model.setPolygons(new ArrayList<>(List.of(polygon1, polygon2)));

        // Удаляем второй полигон и чистим неиспользуемые вершины
        PolygonRemover.processModelAndCleanPolygons(model, Arrays.asList(1), true, false, false);

        // Проверяем, что остался только один полигон
        assertEquals(1, model.getPolygons().size());
        // Проверяем, что остались только нужные вершины (0, 1, 2)
        assertEquals(3, model.getVertices().size());
    }

    @Test
    public void testRemoveAllPolygonsAndCleanEverything() {
        // Создаем модель с 4 вершинами и 2 полигонами
        Model model = new Model();
        model.setVertices(new ArrayList<>(List.of(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 1, 0)
        )));
        model.setTextureVertices(new ArrayList<>(List.of(
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(0, 1),
                new Vector2f(1, 1)
        )));
        model.setNormals(new ArrayList<>(List.of(
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 0, 0)
        )));

        Polygon polygon1 = new Polygon();
        Polygon polygon2 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon1.setTextureVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon1.setNormalIndices(new ArrayList<>(List.of(0, 1, 2)));

        polygon2.setVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        polygon2.setTextureVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        polygon2.setNormalIndices(new ArrayList<>(List.of(1, 2, 0)));

        model.setPolygons(new ArrayList<>(List.of(polygon1, polygon2)));

        // Удаляем все полигоны и чистим все
        PolygonRemover.processModelAndCleanPolygons(model, Arrays.asList(0, 1), true, true, true);

        // Проверяем, что не осталось полигонов
        assertEquals(0, model.getPolygons().size());
        // Проверяем, что все вершины удалены
        assertEquals(0, model.getVertices().size());
        // Проверяем, что все текстурные вершины удалены
        assertEquals(0, model.getTextureVertices().size());
        // Проверяем, что все нормали удалены
        assertEquals(0, model.getNormals().size());
    }
}
