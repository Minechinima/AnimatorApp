package com.minechinima.model.exceptions;

/**
 * Exception that contains a message that can be localized and has a meaning for the user.
 */
public class UserException extends Exception {
    private final String userMessage;

    //TODO localization
    public UserException(String userMessage) {
        super(userMessage);
        this.userMessage = userMessage;
    }

    public UserException(String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
