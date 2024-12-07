package com.cgvsu.nmath;

public class GaussMethod {
    private static boolean equalsZero(double num) {
        return Math.abs(num) < 0.00001;
    }

    public static ResultString[] gaussMethod(double[][] matrix, double[] matrix2)  {

        int matrixHeight = matrix.length;
        int matrixWidth = matrix[0].length;

        // Создаем копии матрицы и вектора свободных членов
        double[][] matrixLeft = new double[matrixHeight][matrixWidth];
        double[] matrixRight = new double[matrixHeight];

        // Копируем значения из исходной матрицы и вектора в копии
        for (int i = 0; i < matrixHeight; i++) {
            matrixRight[i] = matrix2[i];
            System.arraycopy(matrix[i], 0, matrixLeft[i], 0, matrixWidth);
        }

        // Создаем массив для хранения результатов
        ResultString[] results = new ResultString[matrixHeight];
        boolean infiniteResult = false;

        int checkPos = 0;

        // Применяем алгоритм Гаусса для приведения матрицы к ступенчатому виду
        for (int i = 0; i < matrixHeight && checkPos < matrixWidth; checkPos++) {
            for (int j = i + 1; j < matrixHeight && matrixLeft[i][checkPos] == 0; j++) {
                if (!equalsZero(matrixLeft[j][j])) {
                    double[] temp = new double[matrixWidth + 1];
                    System.arraycopy(matrixLeft[j], 0, temp, 0, matrixWidth);
                    temp[matrixWidth] = matrixRight[j];

                    System.arraycopy(matrixLeft[i], 0, matrixLeft[j], 0, matrixWidth);
                    matrixRight[j] = matrixRight[i];

                    System.arraycopy(temp, 0, matrixLeft[i], 0, matrixWidth);
                    matrixRight[i] = temp[matrixWidth];
                }
            }

            // Проверка на нулевую строку и бесконечное количество решений
            if (equalsZero(matrixLeft[i][checkPos])) {
                boolean zeroRow = true;

                for (int j = checkPos + 1; j < matrixWidth; j++) {
                    if (matrixLeft[i][j] != 0) {
                        zeroRow = false;
                        break;
                    }
                }
                if (zeroRow) {
                    if (!equalsZero(matrixRight[i])) {
                        throw new RuntimeException("У системы нет решений");
                    }
                    infiniteResult = true;
                    i++;
                }
                continue;
            }

            // Приведение элемента на главной диагонали к единице
            double c = 1 / matrixLeft[i][checkPos];

            for (int j = checkPos; j < matrixWidth; j++) {
                matrixLeft[i][j] *= c;
            }
            matrixRight[i] *= c;

            // Обнуление элементов ниже главной диагонали
            for (int j = i + 1; j < matrixHeight; j++) {
                if (equalsZero(matrixLeft[j][checkPos])) {
                    continue;
                }

                c = matrixLeft[j][checkPos];

                for (int k = 0; k < matrixWidth; k++) {
                    matrixLeft[j][k] -= c * matrixLeft[i][k];
                }
                matrixRight[j] -= c * matrixRight[i];
            }
            i++;
        }

        // Обратный ход для вычисления значений неизвестных
        for (int i = matrixHeight - 1; i >= 0; i--) {
            // Проверка на нулевой элемент на главной диагонали
            if (equalsZero(matrixLeft[i][i])) {
                if (equalsZero(matrixRight[i])) {
                    results[i] = new ResultString();
                    continue;
                } else {
                    throw new RuntimeException("У системы нет решений");
                }
            }
            if (infiniteResult) {
                // Вычисление значений свободных переменных при бесконечном количестве решений
                ResultString curr = new ResultString(new double[matrixHeight]);
                curr.result = matrixRight[i];

                for (int j = i+1; j < matrixWidth; j++) {
                    if (results[j].isFree) {
                        curr.resultCoefficients[j] -= matrixLeft[i][j];
                    } else {
                        double c = matrixLeft[i][j];
                        for (int k = j; k < matrixHeight; k++) {
                            curr.resultCoefficients[k] -= c * results[j].resultCoefficients[k];
                        }
                        curr.result -= results[j].result * c;
                    }
                }
                results[i] = curr;
            } else {
                // Вычисление значения неизвестной
                double tempResult = matrixRight[i];

                for (int j = i + 1; j < matrixWidth; j++) {
                    tempResult -= matrixLeft[i][j] * results[j].result;
                }

                results[i] = new ResultString(tempResult);
            }
        }

        // Возвращаем результаты
        return results;
    }

    private static String[][] gaussCreateAnswerArray(ResultString[] answer){
        String[][] answerS = new String[2][answer.length];
        for(int i = 0; i<answer.length; i++){
            answerS[0][i] = "x"+(i+1);
            answerS[1][i] = String.valueOf(answer[i]);
        }
        return answerS;
    }
}
