package com.cgvsu.io.objreader.exceptions;

public class FaceWordException extends ObjReaderException {
    public FaceWordException(String type, int lineIndex, int wordIndex) {
        super("Exception in face argument " + wordIndex + ": " + type + " index out of bounds.", lineIndex);
    }
}