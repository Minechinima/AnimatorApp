package com.minechinima.input.raw;

/**
 * Represents an input event that can be sent by the input event manager
 * @param currentKey The key that was pressed or released
 * @param inputType The type of input event
 */
public record KeyEvent(
        Key currentKey,
        InputType inputType,
        long window
) {};
