package com.minechinima.input.raw;

/**
 * Represents a key event
 */
public class KeyEvent extends InputEvent {
    /**
     * The key that was pressed or released
     */
    private final Key currentKey;
    /**
     * The type of input event
     */
    private final InputType inputType;

    /**
     * Creates a new key event
     * @param currentKey The key that was pressed or released
     * @param inputType The type of input event
     * @param window The window that the event was sent from
     */
    public KeyEvent(Key currentKey, InputType inputType, long window) {
        super(window);
        this.currentKey = currentKey;
        this.inputType = inputType;
    }

    /**
     * Gets the key that was pressed or released
     * @return The key that was pressed or released
     */
    public Key getCurrentKey() {
        return currentKey;
    }

    /**
     * Gets the type of input event
     * @return The type of input event
     */
    public InputType getInputType() {
        return inputType;
    }
}
