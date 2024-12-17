package com.cgvsu.io.objreader.exceptions;

public class TokenException extends ObjReaderException {
    public TokenException(int lineIndex) {
        super("Unknown or invalid token.", lineIndex);
    }
}