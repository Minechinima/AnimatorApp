package com.minechinima.input.raw;

/**
 * Represents a mouse scroll event
 */
public class ScrollEvent extends InputEvent {
    /**
     * The horizontal offset of the scroll event
     */
    private final double xOffset;
    /**
     * The vertical offset of the scroll event
     */
    private final double yOffset;

    /**
     * Creates a new scroll event
     * @param xOffset The horizontal offset of the scroll event
     * @param yOffset The vertical offset of the scroll event
     * @param window The window that the event was sent from
     */
    public ScrollEvent(double xOffset, double yOffset, long window) {
        super(window);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /**
     * Gets the horizontal offset of the scroll event
     * @return The horizontal offset of the scroll event
     */
    public double getXOffset() {
        return xOffset;
    }

    /**
     * Gets the vertical offset of the scroll event
     * @return The vertical offset of the scroll event
     */
    public double getYOffset() {
        return yOffset;
    }
}
