package com.cgvsu.io.objreader.exceptions;

public enum ArgumentsErrorType {
    MANY("Too many"),
    FEW("Too few"),
    FEW_IN_POLYGON("Too few face"),
    FEW_IN_WORD("Too few face word"),
    MANY_IN_WORD("Too many face word"),
    NULL("Null");

    private final String textValue;

    ArgumentsErrorType(String textValue) {
        this.textValue = textValue;
    }

    public String getTextValue() {
        return textValue;
    }
}
