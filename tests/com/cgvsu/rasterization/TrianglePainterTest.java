package com.cgvsu.rasterization;

import com.cgvsu.render_engine.PixelWriter;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrianglePainterTest {

    PixelWriter pixelWriter;

    @BeforeEach
    public void setUp() {
        ImageView view = new ImageView();
        view.setFitHeight(100);
        view.setFitWidth(100);
        pixelWriter = new PixelWriter(view);
    }

    @Test
    public void testSort() {
        TrianglePainter painter = new TrianglePainter(
                pixelWriter,
                new int[] {5, -5, 0},
                new int[] {7, 8, -10},
                new double[] {15, 7.9, -0.4}
        );
        painter.sort();
        Assertions.assertArrayEquals(new int[] {0, 5, -5}, painter.arrX);
        Assertions.assertArrayEquals(new int[] {-10, 7, 8}, painter.arrY);
        Assertions.assertArrayEquals(new double[] {-0.4, 15, 7.9}, painter.arrZ);
    }

    @Test
    public void testInterpolateInCorner() {
        TrianglePainter painter = new TrianglePainter(
                pixelWriter,
                new int[] {8, -5, 0},
                new int[] {7, 8, -10},
                new double[] {15, 7.9, -0.4}
        );
        InterpolationResult result = painter.interpolate(8, 7);
        Assertions.assertEquals(15, result.z);
    }

    @Test
    public void testInterpolateInTheMiddleOf2Vertices() {
        TrianglePainter painter = new TrianglePainter(
                pixelWriter,
                new int[] {8, -6, 0},
                new int[] {7, 9, -10},
                new double[] {15, 7.9, -0.4}
        );
        InterpolationResult result = painter.interpolate(1, 8);
        Assertions.assertEquals(11.45, result.z);
    }
}
