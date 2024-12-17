package com.cgvsu.io.objreader;

import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.io.objreader.exceptions.ArgumentsException;
import com.cgvsu.io.objreader.exceptions.FaceTypeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cgvsu.io.objreader.ObjReader.force;

public class ObjReaderTest {

//    @Test
//    void testTooFewVertexArguments() {
//        ObjReader objReader = new ObjReader();
//        final ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1", "2"));
//        try {
//            objReader.parseVertex(wordsInLineWithoutToken, 2);
//            Assertions.fail();
//        } catch (ArgumentsException exception) {
//            String expectedMessage = "Error parsing OBJ file on line: 2. Too few arguments.";
//            Assertions.assertEquals(expectedMessage, exception.getMessage());
//        }
//    }

    @Test
    void testTooManyVertexArguments() {
        ObjReader objReader = new ObjReader();
        force = false;
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0", "3.0", "4.0"));
        try {
            objReader.parseVertex(wordsInLineWithoutToken, 2);
            Assertions.fail();
        } catch (ArgumentsException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 2. Too many arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testTooManyVertexArgumentsSoft() {
        ObjReader objReader = new ObjReader();
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0", "3.0", "4.0"));
        Vector3f result = objReader.parseVertex(wordsInLineWithoutToken, 10);
        Vector3f expected = new Vector3f(1.0F, 2.0F, 3.0F);
        Assertions.assertEquals(expected, result);
    }

//    @Test
//    public void testParseNoVertexArguments() {
//        ObjReader objReader = new ObjReader();
//        final ArrayList<String> wordsInLineWithoutToken = new ArrayList<>();
//        try {
//            objReader.parseVertex(wordsInLineWithoutToken, 2);
//            Assertions.fail();
//        } catch (ObjReaderException exception) {
//            String expectedError = "Error parsing OBJ file on line: 2. Too few arguments.";
//            Assertions.assertEquals(expectedError, exception.getMessage());
//        }
//    }

    @Test
    void testVertex() {
        ObjReader objReader = new ObjReader();
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("2.5", "8", "0"));
        Vector3f expected = new Vector3f(2.5F, 8, 0);
        Vector3f result = objReader.parseVertex(wordsInLineWithoutToken, 10);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTooFewTextureVertexArguments() {
        ObjReader objReader = new ObjReader();
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(List.of("2"));
        try {
            objReader.parseTextureVertex(wordsInLineWithoutToken, 0);
            Assertions.fail();
        } catch (ArgumentsException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 0. Too few arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

//    @Test
//    void testTooManyTextureVertexArguments() {
//        ObjReader objReader = new ObjReader();
//        force = false;
//        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("2.5", "8", "0"));
//        try {
//            objReader.parseTextureVertex(wordsInLineWithoutToken, 2);
//            Assertions.fail();
//        } catch (ArgumentsException exception) {
//            String expectedMessage = "Error parsing OBJ file on line: 2. Too many arguments.";
//            Assertions.assertEquals(expectedMessage, exception.getMessage());
//        }
//    }

    @Test
    void testTooManyTextureVertexArgumentsSoft() {
        ObjReader objReader = new ObjReader();
        force = true;
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("2.5", "8", "0"));
        Vector2f result = objReader.parseTextureVertex(wordsInLineWithoutToken, 2);
        Vector2f expected = new Vector2f(2.5F, 8);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTextureVertex() {
        ObjReader objReader = new ObjReader();
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("2.5", "8"));
        Vector2f expected = new Vector2f(2.5F, 8);
        Vector2f result = objReader.parseTextureVertex(wordsInLineWithoutToken, 10);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testParseFaceArgumentsSizeException() {
        ObjReader objReader = new ObjReader();
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1/1", "2/2"));
        try {
            objReader.parseFace(wordsInLineWithoutToken, 10);
            Assertions.fail();
        } catch (ArgumentsException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 10. Too few face arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testParseFaceTypeException() {
        ObjReader objReader = new ObjReader();
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1", "2/2", "3/3/3"));
        try {
            objReader.parseFace(wordsInLineWithoutToken, 10);
            Assertions.fail();
        } catch (FaceTypeException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 10. Several argument types in one polygon.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }


    @Test
    void testParseVertex() {
        Model model = ObjReader.read("v 0.5 0 1.1");
        Vector3f resultVector = model.getVertices().get(0);

        Vector3f expectedVector = new Vector3f(0.5F, 0F, 1.1F);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedVector, resultVector),
                () -> Assertions.assertEquals(1, model.getVerticesSize()),
                () -> Assertions.assertEquals(0, model.getTextureVerticesSize()),
                () -> Assertions.assertEquals(0, model.getNormalsSize())
        );
    }

    @Test
    void testParseTextureVertex() {
        Model model = ObjReader.read("vt 0 0.7");
        Vector2f resultVector = model.getTextureVertices().get(0);

        Vector2f expectedVector = new Vector2f(0F, 0.7F);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedVector, resultVector),
                () -> Assertions.assertEquals(0, model.getVerticesSize()),
                () -> Assertions.assertEquals(1, model.getTextureVerticesSize()),
                () -> Assertions.assertEquals(0, model.getNormalsSize())
        );
    }

    @Test
    void testParseNormal() {
        Model model = ObjReader.read("vn 0.0 0 -1.1");
        Vector3f resultVector = model.getNormals().get(0);

        Vector3f expectedVector = new Vector3f(0F, 0F, -1.1F);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedVector, resultVector),
                () -> Assertions.assertEquals(0, model.getVerticesSize()),
                () -> Assertions.assertEquals(0, model.getTextureVerticesSize()),
                () -> Assertions.assertEquals(1, model.getNormalsSize())
        );
    }
    @Test
    void testCyrillicAndSpaces() {
        Model model = ObjReader.read(String.valueOf(new File("src/test/resources/ObjFiles/Тест кириллица с пробелами.obj")));
    }
}