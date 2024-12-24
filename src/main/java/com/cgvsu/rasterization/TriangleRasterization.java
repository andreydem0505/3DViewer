package com.cgvsu.rasterization;


public class TriangleRasterization {
    private static float countK(int x1, int y1, int x2, int y2) {
        if (x1 == x2)
            return 0;
        return (float) (y2 - y1) / (x2 - x1);
    }

    private static void drawLine(TrianglePainter trianglePainter, int x1, int x2, int y) {
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        for (int x = x1; x <= x2; x++) {
            trianglePainter.putPixel(x, y);
        }
    }

    public static void fillTriangle(TrianglePainter trianglePainter) {
        trianglePainter.sort();
        int[] arrY = trianglePainter.arrY;
        int[] arrX = trianglePainter.arrX;
        float k1 = countK(arrX[0], arrY[0], arrX[1], arrY[1]);
        float c1 = arrY[0] - k1 * arrX[0];
        float k2 = countK(arrX[0], arrY[0], arrX[2], arrY[2]);
        float c2 = arrY[0] - k2 * arrX[0];
        float k3 = countK(arrX[1], arrY[1], arrX[2], arrY[2]);
        float c3 = arrY[1] - k3 * arrX[1];
        if (arrY[0] == arrY[1]) {
            drawLine(trianglePainter, arrX[0], arrX[1], arrY[1]);
        } else {
            for (int y = arrY[0]; y <= arrY[1]; y++) {
                int x1 = k1 != 0 ? Math.round((y - c1) / k1) : arrX[0];
                int x2 = k2 != 0 ? Math.round((y - c2) / k2) : arrX[0];
                drawLine(trianglePainter, x1, x2, y);
            }
        }
        if (arrY[2] == arrY[1]) {
            drawLine(trianglePainter, arrX[1], arrX[2], arrY[1]);
        } else {
            for (int y = arrY[1]; y <= arrY[2]; y++) {
                int x1 = k3 != 0 ? Math.round((y - c3) / k3) : arrX[1];
                int x2 = k2 != 0 ? Math.round((y - c2) / k2) : arrX[0];
                drawLine(trianglePainter, x1, x2, y);
            }
        }
    }
}
