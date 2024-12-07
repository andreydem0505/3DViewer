package com.cgvsu.nmath;

public interface Vector<V> {
    float x();
    float y();
    float z();
    float w();

    /**
     * Сложение с другим вектором.
     */
    V add(V v2);

    /**
     * Вычитание другого вектора.
     */
    V subtract(V v2);

    /**
     * Умножение вектора на скаляр.
     */
    V scale(float scalar);

    /**
     * Деление вектора на скаляр.
     */
    V divide(float scalar);

    /**
     * Вычисление длины вектора.
     */
    float length();

    /**
     * Нормализация вектора (приведение длины к 1).
     */
    V normalize();

    /**
     * Скалярное произведение с другим вектором.
     */
    float dotProduct(V v2);
}
