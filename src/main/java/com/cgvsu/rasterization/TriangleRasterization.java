package com.cgvsu.rasterization;


public class TriangleRasterization {
    private final TrianglePainter trianglePainter;

    public TriangleRasterization(TrianglePainter trianglePainter) {
        this.trianglePainter = trianglePainter;
    }

    public double[] getLineParameters(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            return new double[]{0, 0};
        }
        double k = (double) (y2 - y1) / (x2 - x1);
        double c = y1 - k * x1;
        return new double[]{k, c};
    }

    private void drawLine(int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            trianglePainter.putPixel(x, y);
        }
    }

    public void fillTriangle() {
        int[] arrY = trianglePainter.arrY;
        int[] arrX = trianglePainter.arrX;
        double[] params1 = getLineParameters(arrX[0], arrY[0], arrX[1], arrY[1]);
        double[] params2 = getLineParameters(arrX[0], arrY[0], arrX[2], arrY[2]);
        double[] params3 = getLineParameters(arrX[1], arrY[1], arrX[2], arrY[2]);
        if (arrY[0] == arrY[1]) {
            drawLine(arrX[0], arrX[1], arrY[1]);
        } else {
            for (int y = arrY[0]; y <= arrY[1]; y++) {
                int x1 = params1[0] != 0 ? (int) ((y - params1[1]) / params1[0]) : arrX[0];
                int x2 = params2[0] != 0 ? (int) ((y - params2[1]) / params2[0]) : arrX[0];
                drawLine(x1, x2, y);
            }
        }
        if (arrY[2] == arrY[1]) {
            drawLine(arrX[1], arrX[2], arrY[1]);
        } else {
            for (int y = arrY[1]; y <= arrY[2]; y++) {
                int x1 = params3[0] != 0 ? (int) ((y - params3[1]) / params3[0]) : arrX[1];
                int x2 = params2[0] != 0 ? (int) ((y - params2[1]) / params2[0]) : arrX[0];
                drawLine(x1, x2, y);
            }
        }
    }
}
