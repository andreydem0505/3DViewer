package com.cgvsu.nmath;

/**
 * Класс Vector3X для работы с трехмерными векторами.
 */
public record Vector3(float x, float y, float z) implements Vector<Vector3> {
    @Override
    public float w() {
        return 0;
    }


    // Реализация методов интерфейса Vector
    @Override
    public Vector3 add(Vector3 v2) {
        return new Vector3(this.x + v2.x, this.y + v2.y, this.z + v2.z);
    }

    @Override
    public Vector3 subtract(Vector3 v2) {
        return new Vector3(this.x - v2.x, this.y - v2.y, this.z - v2.z);
    }

    @Override
    public Vector3 scale(float scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    @Override
    public Vector3 divide(float scalar) {
        if (scalar == 0) {
            throw new ArithmeticException("Vector3X3.divide: деление на ноль невозможно.");
        }
        return new Vector3(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    @Override
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 normalize() {
        float length = length();
        if (length == 0) {
            throw new ArithmeticException("Vector3.normalize: длина вектора равна нулю, нормализация невозможна.");
        }
        return divide(length);
    }

    @Override
    public float dotProduct(Vector3 v2) {
        return this.x * v2.x + this.y * v2.y + this.z * v2.z;
    }

    // Метод для векторного произведения
    public Vector3 crossProduct(Vector3 v2) {
        return new Vector3(
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

        Vector3 vector3X = (Vector3) obj;
        return Math.abs(this.x - vector3X.x) < 1e-6 &&
                Math.abs(this.y - vector3X.y) < 1e-6 &&
                Math.abs(this.z - vector3X.z) < 1e-6;
    }
}
