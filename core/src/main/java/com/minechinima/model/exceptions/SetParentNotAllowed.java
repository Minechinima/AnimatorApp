package com.minechinima.model.exceptions;

public class SetParentNotAllowed extends Exception {
    public SetParentNotAllowed(Throwable cause) {
        super(cause);
    }

    public SetParentNotAllowed(String message) {
        super(message);
    }
}
