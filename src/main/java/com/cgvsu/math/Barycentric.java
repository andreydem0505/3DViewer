package com.cgvsu.math;

import com.cgvsu.nmath.Matrix3x3;
import com.cgvsu.nmath.Vector3f;


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

    public static Vector3f getVector(float[] barycentric, Vector3f[] vectors) {
        return new Vector3f(barycentric[0] * vectors[0].x() +
                barycentric[1] * vectors[1].x() +
                barycentric[2] * vectors[2].x(),
                barycentric[0] * vectors[0].y() +
                        barycentric[1] * vectors[1].y() +
                        barycentric[2] * vectors[2].y(),
                barycentric[0] * vectors[0].z() +
                        barycentric[1] * vectors[1].z() +
                        barycentric[2] * vectors[2].z()
        );
    }
}
