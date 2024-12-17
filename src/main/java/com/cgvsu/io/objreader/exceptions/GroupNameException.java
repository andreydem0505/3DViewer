package com.cgvsu.io.objreader.exceptions;

public class GroupNameException extends ObjReaderException {
    public GroupNameException(int lineIndex) {
        super("Invalid group name.", lineIndex);
    }
}