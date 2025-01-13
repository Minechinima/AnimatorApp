package com.minechinima.input.raw;

/**
 * Represents a mouse movement event
 */
public class MouseEvent extends InputEvent {
    /**
     * The x position of the mouse
     */
    private final double x;
    /**
     * The y position of the mouse
     */
    private final double y;

    /**
     * Creates a new mouse event
     * @param x The x position of the mouse
     * @param y The y position of the mouse
     * @param window The window that the event was sent from
     */
    public MouseEvent(double x, double y, long window) {
        super(window);
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x position of the mouse
     * @return The x position of the mouse
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y position of the mouse
     * @return The y position of the mouse
     */
    public double getY() {
        return y;
    }
}
