package com.cgvsu.triangulation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TriangulationTest {
    @Test
    public void testTriangulation() {
        List<int[]> result = Triangulation.convexPolygonTriangulate(List.of(2, 3, 5, 1, 6));
        Assertions.assertEquals(Set.of(2, 3, 5), Arrays.stream(result.get(0)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(Set.of(2, 5, 1), Arrays.stream(result.get(1)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(Set.of(2, 1, 6), Arrays.stream(result.get(2)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void testTriangulationWithThreeVertices() {
        List<int[]> result = Triangulation.convexPolygonTriangulate(List.of(2, 3, 5));
        Assertions.assertEquals(Set.of(2, 3, 5), Arrays.stream(result.get(0)).boxed().collect(Collectors.toSet()));
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testTriangulationWithLessThanThreeVertices() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Triangulation.convexPolygonTriangulate(List.of(2, 3)));
    }
}
