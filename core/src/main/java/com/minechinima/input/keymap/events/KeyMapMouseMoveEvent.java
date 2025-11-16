package com.minechinima.input.keymap.events;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.ui.elements.window.UIWindow;

import java.util.Objects;

public class KeyMapMouseMoveEvent extends KeyMapEvent {
    private final double prevMouseX;
    private final double prevMouseY;
    private final double dragX;
    private final double dragY;

    public KeyMapMouseMoveEvent(double mouseX, double mouseY,
                                double prevMouseX, double prevMouseY, UIWindow window, PressedKeySet pressedKeys) {
        super(mouseX, mouseY, window, pressedKeys);
        this.prevMouseX = prevMouseX;
        this.prevMouseY = prevMouseY;
        this.dragX = this.mouseX() - this.prevMouseX;
        this.dragY = this.mouseY() - this.prevMouseY;
    }

    public double prevMouseX() {
        return prevMouseX;
    }

    public double prevMouseY() {
        return prevMouseY;
    }

    public double dragX() {
        return dragX;
    }

    public double dragY() {
        return dragY;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof KeyMapMouseMoveEvent that)) return false;
        return Double.compare(prevMouseX, that.prevMouseX) == 0
                && Double.compare(prevMouseY, that.prevMouseY) == 0
                && Double.compare(dragX, that.dragX) == 0
                && Double.compare(dragY, that.dragY) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prevMouseX, prevMouseY, dragX, dragY);
    }

    public enum Direction {
        UP,
        UP_RIGHT,
        RIGHT,
        DOWN_RIGHT,
        DOWN,
        DOWN_LEFT,
        LEFT,
        UP_LEFT;
    }
}
