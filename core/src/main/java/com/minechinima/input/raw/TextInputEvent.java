package com.minechinima.input.raw;

/**
 * Represents a text input event.
 * This differs from {@link KeyEvent} in such a way that the keyboard sends the final character
 * modified by system and pressed keyboard modifier that would be input as text.
 * A text input event has no press or release information.
 */
public class TextInputEvent extends InputEvent {
    private final char character;

    /**
     * Create a new text input event
     *
     * @param character the text input
     * @param window The window that the event was sent from
     */
    public TextInputEvent(char character, long window) {
        super(window);
        this.character = character;
    }

    public char getCharacter() {
        return this.character;
    }
}
