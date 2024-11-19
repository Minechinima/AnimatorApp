package com.minechinima.model.memory;

/**
 * Serious error that should make the app crash.
 * The code needs to be fully robust against this,
 * as undo-redo systems can lead to pretty big duplications in theory.
 * <br><br>
 * Extend from Error, as no reasonable app would catch an Error.
 */
public class MemoryLeakError extends Error {
    public MemoryLeakError() {
        super();
    }

    public MemoryLeakError(String message) {
        super(message);
    }

    public MemoryLeakError(String message, Throwable cause) {
        super(message, cause);
    }

    public MemoryLeakError(Throwable cause) {
        super(cause);
    }
}
