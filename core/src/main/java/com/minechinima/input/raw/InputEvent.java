package com.minechinima.input.raw;

/**
 * Represents an input event that can be sent by the input event manager
 * @param currentKey The key that was pressed or released
 * @param inputType The type of input event
 * @param x The x position of either the mouse or the x offset of the scroll
 * @param y The y position of either the mouse or the y offset of the scroll
 */
public record InputEvent(
        Key currentKey,
        InputType inputType,
        double x,
        double y,
        boolean ctrlPressed,
        boolean shiftPressed,
        boolean altPressed,
        boolean superPressed,
        boolean capsLockPressed,
        boolean numLockPressed,
        long window
) {
    @Override
    public String toString() {
        String string = "";
        if (ctrlPressed) {
            string += "Ctrl + ";
        }
        if (shiftPressed) {
            string += "Shift + ";
        }
        if (altPressed) {
            string += "Alt + ";
        }

        if (inputType == InputType.PRESS || inputType == InputType.RELEASE || inputType == InputType.REPEAT) {
            string += currentKey + " " + inputType;
        } else if(inputType == InputType.MOVE) {
            string += " x: " + x + " y: " + y;
        } else if (inputType == InputType.SCROLL) {
            string += " x: " + x + " y: " + y;
        } else {
            string += inputType;
        }

        return string;
    }
};
