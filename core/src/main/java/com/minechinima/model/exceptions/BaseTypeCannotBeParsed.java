package com.minechinima.model.exceptions;

public class BaseTypeCannotBeParsed extends Exception {
    public BaseTypeCannotBeParsed(String message) {
        super(message);
    }

    public BaseTypeCannotBeParsed(Throwable cause) {
        super(cause);
    }

    public BaseTypeCannotBeParsed(String message, Throwable cause) {
        super(message, cause);
    }
}
