package com.cgvsu.nmath;

public interface Matrix<M, V> {

    /**
     * Сложение с другой матрицей.
     */
    M add(M m2);

    /**
     * Вычитание другой матрицы.
     */
    M subtract(M m2);

    /**
     * Умножение на другую матрицу.
     */
    M multiplyMM(M m2);

    /**
     * Умножение на вектор.
     */
    V multiplyMV(V v2);

    /**
     * Транспонирование матрицы.
     */
    M transpose();

    float get(int i, int j);
}