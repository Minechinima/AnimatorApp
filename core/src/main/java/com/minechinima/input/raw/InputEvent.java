package com.minechinima.input.raw;

/**
 * Represents an input event that can be sent by the input event manager
 */
public abstract class InputEvent {
    /**
     * The window that the event was sent from
     */
    private final long window;

    /**
     * Create a new input event
     * @param window The window that the event was sent from
     */
    protected InputEvent(long window) {
        this.window = window;
    }

    /**
     * Get the window that the event was sent from
     * @return The window that the event was sent from
     */
    public long getWindow() {
        return window;
    }
}
