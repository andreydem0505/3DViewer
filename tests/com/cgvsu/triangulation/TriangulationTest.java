package com.cgvsu.triangulation;

import com.cgvsu.model.Polygon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TriangulationTest {
    private Polygon polygon;

    @BeforeEach
    public void setUp() {
        polygon = new Polygon();
    }

    @Test
    public void testTriangulation() {
        polygon.setVertexIndices(new ArrayList<>(List.of(2, 3, 5, 1, 6)));
        Triangulation.convexPolygonTriangulate(polygon);
        List<int[]> vertices = polygon.getVertexIndicesTriangles();
        Assertions.assertEquals(Set.of(2, 3, 5), Arrays.stream(vertices.get(0)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(Set.of(2, 5, 1), Arrays.stream(vertices.get(1)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(Set.of(2, 1, 6), Arrays.stream(vertices.get(2)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(3, vertices.size());
    }

    @Test
    public void testTriangulationWithThreeVertices() {
        polygon.setVertexIndices(new ArrayList<>(List.of(2, 3, 5)));
        Triangulation.convexPolygonTriangulate(polygon);
        List<int[]> vertices = polygon.getVertexIndicesTriangles();
        Assertions.assertEquals(Set.of(2, 3, 5), Arrays.stream(vertices.get(0)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(1, vertices.size());
    }

    @Test
    public void testTriangulationWithTextureVertices() {
        polygon.setVertexIndices(new ArrayList<>(List.of(2, 3, 5, 1, 6)));
        polygon.setTextureVertexIndices(new ArrayList<>(List.of(4, 8, 9, 7, 6)));
        Triangulation.convexPolygonTriangulate(polygon);
        List<int[]> vertices = polygon.getVertexIndicesTriangles();
        List<int[]> textureVertices = polygon.getTextureVertexIndicesTriangles();

        Assertions.assertEquals(Set.of(2, 3, 5), Arrays.stream(vertices.get(0)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(Set.of(2, 5, 1), Arrays.stream(vertices.get(1)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(Set.of(2, 1, 6), Arrays.stream(vertices.get(2)).boxed().collect(Collectors.toSet()));

        Assertions.assertEquals(Set.of(4, 8, 9), Arrays.stream(textureVertices.get(0)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(Set.of(4, 9, 7), Arrays.stream(textureVertices.get(1)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(Set.of(4, 7, 6), Arrays.stream(textureVertices.get(2)).boxed().collect(Collectors.toSet()));
    }
}
