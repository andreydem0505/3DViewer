package com.cgvsu.rasterization;

import com.cgvsu.render_engine.PixelWriter;

import java.awt.*;

public class Bresenham {
    private static int sign(int x) {
        return Integer.compare(x, 0);
    }

    public static void drawBresenhamLine(PixelWriter pixelWriter, int xstart, int ystart, double zStart, int xend, int yend, double zEnd) {
        int x, y, dx, dy, incx, incy, pdx, pdy, es, el, err;

        dx = xend - xstart;
        dy = yend - ystart;

        incx = sign(dx);
        incy = sign(dy);

        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;

        if (dx > dy) {
            pdx = incx;
            pdy = 0;
            es = dy;
            el = dx;
        } else {
            pdx = 0;
            pdy = incy;
            es = dx;
            el = dy;
        }

        x = xstart;
        y = ystart;
        err = el / 2;
        pixelWriter.putPixel(x, y, zStart, Color.BLACK);

        double pdz = (zEnd - zStart) / el;
        double curZ = zStart;

        for (int t = 0; t < el; t++) {
            err -= es;
            if (err < 0) {
                err += el;
                x += incx;
                y += incy;
            } else {
                x += pdx;
                y += pdy;
                curZ += pdz;
            }

            pixelWriter.putPixel(x, y, curZ, Color.BLACK);
        }
    }
}
