package com.cgvsu.nmath;

/**
 * Класс Vector4X для работы с четырехмерными векторами.
 */
public class Vector4f implements Vector<Vector4f> {
    private float x, y, z, w;
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
        return w;
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector4f source){
        this.x = source.x();
        this.y = source.y();
        this.z = source.z();
        this.w = source.w();
    }

    // Реализация методов интерфейса Vector
    @Override
    public Vector4f add(Vector4f v2) {
        this.x += v2.x;
        this.y += v2.y;
        this.z += v2.z;
        this.w += v2.w;
        return this;
    }

    @Override
    public Vector4f subtract(Vector4f v2) {
        this.x -= v2.x;
        this.y -= v2.y;
        this.z -= v2.z;
        this.w -= v2.w;
        return this;
    }

    @Override
    public Vector4f scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }

    @Override
    public Vector4f divide(float scalar) {
        if (scalar == 0) {
            throw new ArithmeticException("Vector4X.divide: деление на ноль невозможно.");
        }
        this.x /= scalar;
        this.y /= scalar;
        this.z /= scalar;
        this.w /= scalar;
        return this;
    }

    @Override
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    @Override
    public Vector4f normalize() {
        float length = length();
        if (length == 0) {
            throw new ArithmeticException("Vector4X.normalize: длина вектора равна нулю, нормализация невозможна.");
        }
        return divide(length);
    }

    @Override
    public float dotProduct(Vector4f v2) {
        return this.x * v2.x + this.y * v2.y + this.z * v2.z + this.w * v2.w;
    }

    @Override
    public String toString() {
        return "Vector4X{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Vector4f vector = (Vector4f) obj;
        return Math.abs(this.x - vector.x) < 1e-6 &&
                Math.abs(this.y - vector.y) < 1e-6 &&
                Math.abs(this.z - vector.z) < 1e-6 &&
                Math.abs(this.w - vector.w) < 1e-6;
    }
}
