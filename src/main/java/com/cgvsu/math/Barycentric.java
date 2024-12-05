package com.cgvsu.math;

public class Barycentric {
    public static float[] calculate(int x, int y, int[] arrX, int[] arrY) {
        return Matrix.solveKramer3By3(new int[][]{
                    {arrX[0], arrX[1], arrX[2], x},
                    {arrY[0], arrY[1], arrY[2], y},
                    {1, 1, 1, 1},
            });
    }
}
