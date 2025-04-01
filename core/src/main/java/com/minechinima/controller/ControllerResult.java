package com.minechinima.controller;

import org.jetbrains.annotations.Nullable;

/**
 * Every method of the Controller Layer must return this
 * @param result
 * @param userMessage
 * @param exception
 */
public record ControllerResult(Transaction transaction, State state, String userMessage, @Nullable Exception exception) {
    public ControllerResult {
        if (state != State.ERROR && exception != null) {
            throw new IllegalArgumentException("Argument value mismatch: Exceptions can only happen with ERROR status");
        }
    }

    public ControllerResult(Transaction transaction, State result, String userMessage) {
        this(transaction, result, userMessage, null);
    }

    public static enum State {
        /**
         * Controller method started execution, but something expected or unexpected happened.
         * This can include exceptions.
         */
        ERROR,
        SUCCESS
    }
}
