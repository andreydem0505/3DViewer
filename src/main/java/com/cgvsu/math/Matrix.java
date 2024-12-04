package com.cgvsu.math;


public class Matrix {
    public static class MatrixSizeException extends Exception {}

    public static double[] solveGauss(int[][] matrix) throws MatrixSizeException {
        if (matrix.length != matrix[0].length - 1)
            throw new MatrixSizeException();

        double[][] resultMatrix = new double[matrix.length][matrix.length + 1];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length + 1; j++) {
                resultMatrix[i][j] = matrix[i][j];
            }
        }

        double coefficient;
        for (int j = 0; j < resultMatrix.length - 1; j++) {
            for (int i = j + 1; i < resultMatrix.length; i++) {
                if (resultMatrix[i][j] != 0) {
                    coefficient = resultMatrix[j][j] / resultMatrix[i][j];
                    for (int k = j; k <= resultMatrix.length; k++) {
                        resultMatrix[i][k] = resultMatrix[i][k] * coefficient - resultMatrix[j][k];
                    }
                }
            }
        }

        double[] result = new double[matrix.length];
        for (int i = matrix.length - 1; i > -1; i--) {
            if (resultMatrix[i][i] == 0) {
                result[i] = 0;
                continue;
            }

            for (int j = matrix.length - 1; j > i; j--) {
                resultMatrix[i][matrix.length] -= result[j] * resultMatrix[i][j];
            }
            result[i] = resultMatrix[i][matrix.length] / resultMatrix[i][i];
        }

        return result;
    }

    private static double getDeterminant3By3(double[][] arr) {
        return arr[0][0] * arr[1][1] * arr[2][2] + arr[1][0] * arr[0][2] * arr[2][1] +
                arr[0][1] * arr[1][2] * arr[2][0] - arr[0][2] * arr[1][1] * arr[2][0] -
                arr[0][0] * arr[1][2] * arr[2][1] - arr[0][1] * arr[1][0] * arr[2][2];
    }

    public static double[] solveKramer3By3(int[][] matrix) {
        double determinant = getDeterminant3By3(new double[][]{
                {matrix[0][0], matrix[0][1], matrix[0][2]},
                {matrix[1][0], matrix[1][1], matrix[1][2]},
                {matrix[2][0], matrix[2][1], matrix[2][2]},
        });
        double determinant1 = getDeterminant3By3(new double[][]{
                {matrix[0][3], matrix[0][1], matrix[0][2]},
                {matrix[1][3], matrix[1][1], matrix[1][2]},
                {matrix[2][3], matrix[2][1], matrix[2][2]},
        });
        double determinant2 = getDeterminant3By3(new double[][]{
                {matrix[0][0], matrix[0][3], matrix[0][2]},
                {matrix[1][0], matrix[1][3], matrix[1][2]},
                {matrix[2][0], matrix[2][3], matrix[2][2]},
        });
        double determinant3 = getDeterminant3By3(new double[][]{
                {matrix[0][0], matrix[0][1], matrix[0][3]},
                {matrix[1][0], matrix[1][1], matrix[1][3]},
                {matrix[2][0], matrix[2][1], matrix[2][3]},
        });
        return new double[]{determinant1 / determinant, determinant2 / determinant, determinant3 / determinant};
    }
}
