package com.cgvsu.io.objreader;

import com.cgvsu.io.objreader.exceptions.ArgumentsException;
import com.cgvsu.io.objreader.exceptions.ParsingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FaceWordTest {
    @Test
    void testVertex1() {
        FaceWord faceWord = FaceWord.parse("2", 10);
        Assertions.assertAll(
                () -> Assertions.assertEquals(WordType.VERTEX, faceWord.getWordType()),
                () -> Assertions.assertEquals(1, faceWord.getVertexIndex()),
                () -> Assertions.assertNull(faceWord.getTextureVertexIndex()),
                () -> Assertions.assertNull(faceWord.getNormalIndex())
        );
    }

    @Test
    void testVertex2() {
        FaceWord faceWord = FaceWord.parse("3//////", 10);
        Assertions.assertAll(
                () -> Assertions.assertEquals(WordType.VERTEX, faceWord.getWordType()),
                () -> Assertions.assertEquals(2, faceWord.getVertexIndex()),
                () -> Assertions.assertNull(faceWord.getTextureVertexIndex()),
                () -> Assertions.assertNull(faceWord.getNormalIndex())
        );
    }

    @Test
    void testVertexTexture() {
        FaceWord faceWord = FaceWord.parse("4/5", 10);
        Assertions.assertAll(
                () -> Assertions.assertEquals(WordType.VERTEX_TEXTURE, faceWord.getWordType()),
                () -> Assertions.assertEquals(3, faceWord.getVertexIndex()),
                () -> Assertions.assertEquals(4, faceWord.getTextureVertexIndex()),
                () -> Assertions.assertNull(faceWord.getNormalIndex())
        );
    }

    @Test
    void testVertexNormal() {
        FaceWord faceWord = FaceWord.parse("1//2", 10);
        Assertions.assertAll(
                () -> Assertions.assertEquals(WordType.VERTEX_NORMAL, faceWord.getWordType()),
                () -> Assertions.assertEquals(0, faceWord.getVertexIndex()),
                () -> Assertions.assertNull(faceWord.getTextureVertexIndex()),
                () -> Assertions.assertEquals(1, faceWord.getNormalIndex())
        );
    }

    @Test
    void testVertexTextureNormal() {
        FaceWord faceWord = FaceWord.parse("4/5/6", 10);
        Assertions.assertAll(
                () -> Assertions.assertEquals(WordType.VERTEX_TEXTURE_NORMAL, faceWord.getWordType()),
                () -> Assertions.assertEquals(3, faceWord.getVertexIndex()),
                () -> Assertions.assertEquals(4, faceWord.getTextureVertexIndex()),
                () -> Assertions.assertEquals(5, faceWord.getNormalIndex())
        );
    }

    @Test
    void testParseVertexException1() {
        try {
            FaceWord.parse("4,54/5/6", 10);
            Assertions.fail();
        } catch (ParsingException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 10. Failed to parse integer value.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testParseVertexException2() {
        try {
            FaceWord.parse("/5/6", 10);
            Assertions.fail();
        } catch (ParsingException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 10. Failed to parse integer value.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testParseTextureVertexException() {
        try {
            FaceWord.parse("2/1,111/1", 10);
            Assertions.fail();
        } catch (ParsingException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 10. Failed to parse integer value.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testParseNormalException() {
        try {
            FaceWord.parse("1/4.123/2", 10);
            Assertions.fail();
        } catch (ParsingException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 10. Failed to parse integer value.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testTooFewArguments() {
        try {
            FaceWord.parse("////", 10);
            Assertions.fail();
        } catch (ArgumentsException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 10. Too few face word arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testTooManyArguments() {
        try {
            FaceWord.parse("1/2/3/4", 10, false);
            Assertions.fail();
        } catch (ArgumentsException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 10. Too many face word arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testTooManyArgumentsSoft() {
        FaceWord faceWord = FaceWord.parse("1/2/3/4", 10);
        Assertions.assertAll(
                () -> Assertions.assertEquals(WordType.VERTEX_TEXTURE_NORMAL, faceWord.getWordType()),
                () -> Assertions.assertEquals(0, faceWord.getVertexIndex()),
                () -> Assertions.assertEquals(1, faceWord.getTextureVertexIndex()),
                () -> Assertions.assertEquals(2, faceWord.getNormalIndex())
        );
    }
}
