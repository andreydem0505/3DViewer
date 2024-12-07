package com.cgvsu.nmath;

/**
 * Класс Matrix3x3 для работы с матрицами размером 3x3.
 */
public class Matrix3x3 implements Matrix<Matrix3x3, Vector3f> {
    private final float[] elements;

    // Конструктор
    public Matrix3x3(float[] elements) {
        if (elements == null || elements.length != 9) {
            throw new IllegalArgumentException("Matrix3x3X: некорректные размеры матрицы.");
        }
        this.elements = new float[9];
        System.arraycopy(elements, 0, this.elements, 0, 9);
    }

    // Реализация методов интерфейса Matrix
    @Override
    public Matrix3x3 add(Matrix3x3 m2) {
        float[] result = new float[9];
        for (int i = 0; i < 9; i++) {
            result[i] = this.elements[i] + m2.elements[i];
        }
        return new Matrix3x3(result);
    }

    @Override
    public Matrix3x3 subtract(Matrix3x3 m2) {
        float[] result = new float[9];
        for (int i = 0; i < 9; i++) {
            result[i] = this.elements[i] - m2.elements[i];
        }
        return new Matrix3x3(result);
    }

    @Override
    public Matrix3x3 multiplyMM(Matrix3x3 m2) {
        float[] result = new float[9];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i * 3 + j] = 0;
                for (int k = 0; k < 3; k++) {
                    result[i * 3 + j] += this.elements[i * 3 + k] * m2.elements[k * 3 + j];
                }
            }
        }
        return new Matrix3x3(result);
    }

    @Override
    public Vector3f multiplyMV(Vector3f v2) {
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = this.elements[i * 3] * v2.x() +
                    this.elements[i * 3 + 1] * v2.y() +
                    this.elements[i * 3 + 2] * v2.z();
        }
        return new Vector3f(result[0], result[1], result[2]);

    }

    public Matrix3x3 transpose() {
        float[] result = new float[9];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i * 3 + j] = this.elements[j * 3 + i];
            }
        }
        return new Matrix3x3(result);
    }

    public static Matrix3x3 identity() {
        return new Matrix3x3(new float[]{1, 0, 0, 0, 1, 0, 0, 0, 1});
    }

    public static Matrix3x3 zero() {
        return new Matrix3x3(new float[9]);
    }

    public float determinant() {
        return elements[0] * (elements[4] * elements[8] - elements[5] * elements[7])
                - elements[1] * (elements[3] * elements[8] - elements[5] * elements[6])
                + elements[2] * (elements[3] * elements[7] - elements[4] * elements[6]);
    }

    public Matrix3x3 inverse() {
        float det = determinant();
        if (Math.abs(det) < 1e-6) {
            throw new IllegalArgumentException("Matrix3x3X.inverse: матрица вырождена.");
        }
        float[] result = new float[9];
        result[0] = (elements[4] * elements[8] - elements[5] * elements[7]) / det;
        result[1] = (elements[2] * elements[7] - elements[1] * elements[8]) / det;
        result[2] = (elements[1] * elements[5] - elements[2] * elements[4]) / det;
        result[3] = (elements[5] * elements[6] - elements[3] * elements[8]) / det;
        result[4] = (elements[0] * elements[8] - elements[2] * elements[6]) / det;
        result[5] = (elements[2] * elements[3] - elements[0] * elements[5]) / det;
        result[6] = (elements[3] * elements[7] - elements[4] * elements[6]) / det;
        result[7] = (elements[1] * elements[6] - elements[0] * elements[7]) / det;
        result[8] = (elements[0] * elements[4] - elements[1] * elements[3]) / det;
        return new Matrix3x3(result);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Matrix3x3X{\n");
        for (int i = 0; i < 3; i++) {
            sb.append("  [");
            for (int j = 0; j < 3; j++) {
                sb.append(elements[i * 3 + j]).append(j < 2 ? ", " : "");
            }
            sb.append("]\n");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Matrix3x3 other)) return false;
        for (int i = 0; i < 9; i++) {
            if (Math.abs(this.elements[i] - other.elements[i]) > 1e-6) {
                return false;
            }
        }
        return true;
    }
}