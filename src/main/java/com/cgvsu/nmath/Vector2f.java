package com.cgvsu.nmath;

/**
 * Класс Vector2X для работы с двухмерными векторами.
 */
public class Vector2f implements Vector<Vector2f> {
    private float x, y;

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
        return 0;
    }

    @Override
    public float w() {
        return 0;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f source){
        this.x = source.x();
        this.y = source.y();
    }

    // Реализация методов интерфейса Vector2X
    @Override
    public Vector2f add(Vector2f v2) {
        this.x += v2.x;
        this.y += v2.y;
        return this;
    }

    @Override
    public Vector2f subtract(Vector2f v2) {
        this.x -= v2.x;
        this.y -= v2.y;
        return this;
    }

    @Override
    public Vector2f scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    @Override
    public Vector2f divide(float scalar) {
        if (scalar == 0) {
            throw new ArithmeticException("Vector2X.divide: деление на ноль невозможно.");
        }
        this.x /= scalar;
        this.y /= scalar;
        return this;
    }

    @Override
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public Vector2f normalize() {
        float length = length();
        if (length == 0) {
            throw new ArithmeticException("Vector2X.normalize: длина вектора равна нулю, нормализация невозможна.");
        }
        return divide(length);
    }

    @Override
    public float dotProduct(Vector2f v2) {
        return this.x * v2.x + this.y * v2.y;
    }

    @Override
    public String toString() {
        return "Vector2X{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Vector2f vector2x = (Vector2f) obj;
        return Math.abs(this.x - vector2x.x) < 1e-6 && Math.abs(this.y - vector2x.y) < 1e-6;
    }
}
