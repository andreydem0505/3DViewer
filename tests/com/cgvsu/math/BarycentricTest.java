package com.cgvsu.math;

import com.cgvsu.nmath.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BarycentricTest {
    @Test
    public void testCalculateInCorner() {
        float[] result = Barycentric.calculate(1, 1, new int[] {1, -3, 6}, new int[] {1, 2, -6});
        Assertions.assertTrue(Linal.floatEquals(result[0], 1));
        Assertions.assertTrue(Linal.floatEquals(result[1], 0));
        Assertions.assertTrue(Linal.floatEquals(result[2], 0));
    }

    @Test
    public void testCalculateInTheMiddleOf2Vertices() {
        float[] result = Barycentric.calculate(2, 3, new int[] {0, 4, 2}, new int[] {5, 5, 0});
        Assertions.assertTrue(Linal.floatEquals(result[0], result[1]));
    }

    @Test
    public void testBarycentricSum() {
        for (int i = -10; i <= 10; i++) {
            for (int j = -10; j <= 10; j++) {
                float[] result = Barycentric.calculate(i, j, new int[]{1, 2, 3}, new int[]{1, 2, -3});
                Assertions.assertTrue(Linal.floatEquals(result[0] + result[1] + result[2], 1));
            }
        }
    }

    @Test
    public void testBarycentricOnLine() {
        float[] result = Barycentric.calculate(1, 1, new int[]{1, 2, 3}, new int[]{1, 2, 3});
        Assertions.assertTrue(Linal.floatEquals(result[0], 0));
        Assertions.assertTrue(Linal.floatEquals(result[1], 0));
        Assertions.assertTrue(Linal.floatEquals(result[2], 0));
    }

    @Test
    public void testGetVectorInCorner() {
        Vector3f vector = Barycentric.getVector(new float[] {1, 0, 0}, new Vector3f[]{
                new Vector3f(7, 8, 9),
                new Vector3f(5, -8, 10),
                new Vector3f(0, 0, 0)
        });
        Assertions.assertTrue(Linal.floatEquals(vector.x(), 7));
        Assertions.assertTrue(Linal.floatEquals(vector.y(), 8));
        Assertions.assertTrue(Linal.floatEquals(vector.z(), 9));
    }

    @Test
    public void testGetVectorInTheMiddleOf2Vertices() {
        Vector3f vector = Barycentric.getVector(new float[] {0.5f, 0.5f, 0}, new Vector3f[]{
                new Vector3f(7, 8, 9),
                new Vector3f(5, -8, 10),
                new Vector3f(0, 0, 0)
        });
        Assertions.assertTrue(Linal.floatEquals(vector.x(), 6));
        Assertions.assertTrue(Linal.floatEquals(vector.y(), 0));
        Assertions.assertTrue(Linal.floatEquals(vector.z(), 9.5f));
    }
}
