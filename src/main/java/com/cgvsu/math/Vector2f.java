package com.cgvsu.math;

import java.util.Objects;

// Это заготовка для собственной библиотеки для работы с линейной алгеброй
public class Vector2f {
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Vector2f vector2f = (Vector2f) object;
        return Math.abs(x - vector2f.x) < Linal.eps && Math.abs(y - vector2f.y) < Linal.eps;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public float x, y;
}
