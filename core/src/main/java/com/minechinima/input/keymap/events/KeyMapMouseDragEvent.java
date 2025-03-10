package com.minechinima.input.keymap.events;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.raw.Key;
import com.minechinima.ui.elements.UIWindow;

/**
 * Triggered for a key when a certain threshold of mouse movement.
 */
public class KeyMapMouseDragEvent extends KeyMapMouseMoveEvent {
    private final Key key;
    private final Direction direction;

    public KeyMapMouseDragEvent(Key key, double mouseX, double mouseY, double prevMouseX, double prevMouseY, Direction direction, UIWindow window, PressedKeySet pressedKeys) {
        super(mouseX, mouseY, prevMouseX, prevMouseY, window, pressedKeys);
        this.key = key;
        this.direction = direction;
    }

    public Direction direction() {
        return this.direction;
    }

    public Key key() {
        return this.key;
    }
}
