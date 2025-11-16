package com.minechinima.input.keymap.events;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.raw.Key;
import com.minechinima.ui.elements.window.UIWindow;
import com.minechinima.utils.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Triggered for a key when a certain threshold of mouse movement.
 */
public class KeyMapMouseDragEvent extends KeyMapMouseMoveEvent {
    /**
     * Store the event here to allow custom data
     */
    private final Key key;
    private final Direction direction;

    private final Set<Pair<Key, Direction>> keys = new HashSet<>();

    public KeyMapMouseDragEvent(Key key, double mouseX, double mouseY, double prevMouseX, double prevMouseY, Direction direction,
                                UIWindow window, PressedKeySet pressedKeys) {
        super(mouseX, mouseY, prevMouseX, prevMouseY, window, pressedKeys);
        this.key = key;
        this.direction = direction;
    }

    public Direction direction() {
        return this.direction;
    }

    /**
     * @return the keyevent for which the dragging threshold was surpassed.
     * This is a cached event and might contain outdated information.
     */
    public Key key() {
        return this.key;
    }
}
