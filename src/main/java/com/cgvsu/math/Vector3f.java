package com.cgvsu.math;

import java.util.Objects;

// Это заготовка для собственной библиотеки для работы с линейной алгеброй
public class Vector3f {
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Vector3f vector3f = (Vector3f) object;
        return Math.abs(x - vector3f.x) < Linal.eps &&
                Math.abs(y - vector3f.y) < Linal.eps &&
                Math.abs(z - vector3f.z) < Linal.eps;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    public float x, y, z;
}
