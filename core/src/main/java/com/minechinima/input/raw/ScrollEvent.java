package com.minechinima.input.raw;

/**
 * Represents an input event that can be sent by the input event manager
 * @param xOffset The x offset of the scroll
 * @param yOffset The y offset of the scroll
 */
public record ScrollEvent(
        double xOffset,
        double yOffset,
        long window
) {};
