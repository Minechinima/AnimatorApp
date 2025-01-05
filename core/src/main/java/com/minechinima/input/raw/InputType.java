package com.minechinima.input.raw;

/**
 * Represents the type of input event
 */
public enum InputType {
    /**
     * Represents an unknown input event
     */
    UNKNOWN,
    /**
     * Represents a key press (keyboard or mouse)
     */
    PRESS,
    /**
     * Represents a key release (keyboard or mouse)
     */
    RELEASE,
    /**
     * Represents a key repeat (keyboard)
     */
    REPEAT,
    /**
     * Represents a mouse scroll
     */
    SCROLL,
    /**
     * Represents a mouse move
     */
    MOVE;
}
