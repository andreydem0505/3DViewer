package com.cgvsu.io.objreader;

import com.cgvsu.math.Linal;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Locale;

public class LinalTest {
    @Test
    public void testVectorProduct01() {
        Vector3f a = new Vector3f(1f, 0f, 0f);
        Vector3f b = new Vector3f(0f, 1f, 0f);

        Vector3f c = Linal.crossProduct(a, b);
        Vector3f expected = new Vector3f(0f, 0f, 1f);
        Assertions.assertTrue(c.equals(expected));
    }

    @Test
    public void testVectorProduct02() {
        Vector3f a = new Vector3f(1f, 0f, 0f);
        Vector3f b = new Vector3f(1f, 0f, 0f);

        Vector3f c = Linal.crossProduct(a, b);
        Vector3f expected = new Vector3f(0f, 0f, 0f);
        Assertions.assertTrue(c.equals(expected));
    }

    @Test
    public void testNormalize01(){
        boolean failed = false;
        try {
            Vector3f a = new Vector3f(0f, 0f, 0f);
            Vector3f b = Linal.normalize(a);
            Vector3f expected = new Vector3f(0f, 0f, 0f);
            Assertions.assertTrue(b.equals(expected));
        }catch (ArithmeticException e){
            failed = true; // planned failing
        }
        Assertions.assertFalse(failed);
    }

//    @Test
//    public void testNormals01(){
//        Locale.setDefault(Locale.ROOT);
//        String data =
//                "v 1  1  -1\n" +
//                        "v 1  -1 -1\n" +
//                        "v 1  1  1\n" +
//                        "v 1  -1 1\n" +
//                        "v -1  1 -1\n" +
//                        "v -1 -1 -1\n" +
//                        "v -1  1 1\n" +
//                        "v -1 -1 1\n" +
//                        "f 1/1/1 5/2/5 7/3/7 3/4/3\n" +
//                        "f 4/5/4 3/4/3 7/6/7 8/7/8\n" +
//                        "f 8/8/8 7/9/7 5/10/5 6/11/6\n" +
//                        "f 6/12/6 2/13/2 4/5/4 8/14/8\n" +
//                        "f 2/13/2 1/1/1 3/4/3 4/5/4\n" +
//                        "f 6/11/6 5/10/5 1/1/1 2/13/2";
//        Model model = ObjReader.read(data);
//        model.normals = Linal.calculateVerticesNormals(model.vertices, model.polygons);
//        String result = ObjWriter.convertNormals(model);
//        String expected =
//                "vn 0.5774 0.5774 -0.5774\n" +
//                        "vn 0.5774 -0.5774 -0.5774\n" +
//                        "vn 0.5774 0.5774 0.5774\n" +
//                        "vn 0.5774 -0.5774 0.5774\n" +
//                        "vn -0.5774 0.5774 -0.5774\n" +
//                        "vn -0.5774 -0.5774 -0.5774\n" +
//                        "vn -0.5774 0.5774 0.5774\n" +
//                        "vn -0.5774 -0.5774 0.5774\n";
//        Assertions.assertEquals(expected, result);
//    }
//
//    @Test
//    public void testNormals02(){
//        Locale.setDefault(Locale.ROOT);
//        String data =
//                "v 1.000000 1.000000 -1.000000\n" +
//                        "v 1.000000 -1.000000 -1.000000\n" +
//                        "v 1.000000 1.000000 1.000000\n" +
//                        "v 1.000000 -1.000000 1.000000\n" +
//                        "v -1.000000 1.000000 -1.000000\n" +
//                        "v -1.000000 -1.000000 -1.000000\n" +
//                        "v -1.000000 1.000000 1.000000\n" +
//                        "v -1.000000 -1.000000 1.000000\n" +
//                        "f 1/1/1 5/2/5 7/3/7 3/4/3\n" +
//                        "f 4/5/4 3/4/3 7/6/7 8/7/8\n" +
//                        "f 8/8/8 7/9/7 5/10/5 6/11/6\n" +
//                        "f 6/12/6 2/13/2 4/5/4 8/14/8\n" +
//                        "f 2/13/2 1/1/1 3/4/3 4/5/4\n" +
//                        "f 6/11/6 5/10/5 1/1/1 2/13/2";
//        Model model = ObjReader.read(data);
//        model.normals = Linal.calculatePolygonNormals(model.vertices, model.polygons);
//        for (int i = 0; i < model.polygons.size(); i++){
//            Polygon polygon = model.polygons.get(i);
//            Collections.fill(polygon.getNormalIndices(), i);
//        }
//        String result = ObjWriter.convertNormals(model);
//        String expected =
//                "vn 0.0000 1.0000 0.0000\n" +
//                        "vn 0.0000 0.0000 1.0000\n" +
//                        "vn -1.0000 -0.0000 -0.0000\n" +
//                        "vn 0.0000 -1.0000 0.0000\n" +
//                        "vn 1.0000 0.0000 -0.0000\n" +
//                        "vn 0.0000 0.0000 -1.0000\n";
//        Assertions.assertEquals(expected, result);
//    }
}

