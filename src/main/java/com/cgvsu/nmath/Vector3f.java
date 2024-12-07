package com.cgvsu.nmath;

/**
 * Класс Vector3X для работы с трехмерными векторами.
 */
public class Vector3f implements Vector<Vector3f> {
    private float x, y, z;
    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public float z() {
        return z;
    }

    @Override
    public float w() {
        return 0;
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f source){
        this.x = source.x();
        this.y = source.y();
        this.z = source.z();
    }


    // Реализация методов интерфейса Vector
    @Override
    public Vector3f add(Vector3f v2) {
        this.x += v2.x;
        this.y += v2.y;
        this.z += v2.z;
        return this;
    }

    @Override
    public Vector3f subtract(Vector3f v2) {
        this.x -= v2.x;
        this.y -= v2.y;
        this.z -= v2.z;
        return this;
    }

    @Override
    public Vector3f scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    @Override
    public Vector3f divide(float scalar) {
        if (scalar == 0) {
            throw new ArithmeticException("Vector3X3.divide: деление на ноль невозможно.");
        }
        this.x /= scalar;
        this.y /= scalar;
        this.z /= scalar;
        return this;
    }

    @Override
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f normalize() {
        float length = length();
        if (length == 0) {
            throw new ArithmeticException("Vector3.normalize: длина вектора равна нулю, нормализация невозможна.");
        }
        return divide(length);
    }

    @Override
    public float dotProduct(Vector3f v2) {
        return this.x * v2.x + this.y * v2.y + this.z * v2.z;
    }

    // Метод для векторного произведения
    public Vector3f crossProduct(Vector3f v2) {
        return new Vector3f(
                this.y * v2.z - this.z * v2.y,
                this.z * v2.x - this.x * v2.z,
                this.x * v2.y - this.y * v2.x
        );
    }

    @Override
    public String toString() {
        return "Vector3X3{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Vector3f vector3X = (Vector3f) obj;
        return Math.abs(this.x - vector3X.x) < 1e-6 &&
                Math.abs(this.y - vector3X.y) < 1e-6 &&
                Math.abs(this.z - vector3X.z) < 1e-6;
    }
}
