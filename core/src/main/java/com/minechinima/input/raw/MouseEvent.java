package com.minechinima.input.raw;

/**
 * Represents an input event that can be sent by the input event manager
 * @param x The x position of either the mouse
 * @param y The y position of either the mouse
 */
public record MouseEvent(
        double x,
        double y,
        long window
) {};
