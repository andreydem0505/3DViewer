package com.cgvsu.nmath;

import java.util.StringJoiner;

public class ResultString {

    public boolean isFree;  // true, если корень является свободным
    public boolean isInfinite;  // корень выражается через другие свободные корни
    double[] resultCoefficients;  // массив: [{свободный коэффициент}, {коэффициент при x[i]}, ...]
    public double result;

    public ResultString(double[] arr) {
        isInfinite = true;
        isFree = false;

        resultCoefficients = new double[arr.length];
        System.arraycopy(arr, 0, resultCoefficients, 0, arr.length);
    }

    public ResultString(double result) {
        isInfinite = false;
        isFree = false;

        this.result = result;
    }

    public ResultString() {
        isFree = true;
    }

    public double[] getResultCoefficients() {
        return resultCoefficients;
    }

    @Override
    public String toString() {
        if (isFree) {
            return "(-00; +00)";
        } else if (isInfinite) {
            StringJoiner sj = new StringJoiner(" + ");

            sj.add(String.format("%.3f", result));

            for (int i = 0; i < resultCoefficients.length; i++) {
                if (resultCoefficients[i] != 0) {
                    sj.add(String.format("%.3f", resultCoefficients[i]) + "x" + (i + 1));
                }
            }

            return sj.toString();
        } else {
            return String.format("%.3f", result);
        }
    }

}
