package com.cgvsu.math;

import com.cgvsu.nmath.Matrix3x3;
import com.cgvsu.nmath.Vector3f;

import java.util.Arrays;

public class Barycentric {
    public static float[] calculate(int x, int y, int[] arrX, int[] arrY) {
        Matrix3x3 coefs = new Matrix3x3(new float[]{
                arrX[0], arrX[1], arrX[2],
                arrY[0], arrY[1], arrY[2],
                1, 1, 1
        });
        Vector3f adjugate = new Vector3f(x, y, 1);
        return Linal.solveKramer3x3(coefs, adjugate);
    }
}
