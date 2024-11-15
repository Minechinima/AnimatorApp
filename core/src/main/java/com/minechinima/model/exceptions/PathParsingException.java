package com.minechinima.model.exceptions;

public class PathParsingException extends Exception {
    public PathParsingException(Throwable cause) {
        super(cause);
    }

    public PathParsingException(String message) {
        super(message);
    }
}
