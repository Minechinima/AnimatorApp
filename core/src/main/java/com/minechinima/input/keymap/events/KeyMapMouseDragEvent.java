package com.minechinima.input.keymap.events;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.raw.IKey;
import com.minechinima.input.raw.Key;
import com.minechinima.ui.elements.window.UIWindow;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Triggered for a key when a certain threshold of mouse movement.
 */
public class KeyMapMouseDragEvent extends KeyMapMouseMoveEvent implements IKeyMapKeyEvent {
    private final Map<KeyMapKeyEvent, Direction> keys = new HashMap<>();
    private final Direction currentDirection;

    public KeyMapMouseDragEvent(double mouseX, double mouseY, double prevMouseX, double prevMouseY,
                                UIWindow window, PressedKeySet pressedKeySet,
                                Map<KeyMapKeyEvent, Direction> draggedKeys,
                                Direction currentDirection) {
        super(mouseX, mouseY, prevMouseX, prevMouseY, window, pressedKeySet);
        this.keys.putAll(draggedKeys);
        this.currentDirection = currentDirection;
    }

    public Map<KeyMapKeyEvent, Direction> getDraggedKeys() {
        Map<KeyMapKeyEvent, Direction> keysCopy = new HashMap<>();
        keysCopy.putAll(keys);
        return keysCopy;
    }

    public boolean isKeyDragged(IKey searchKey) {
        for (Map.Entry<KeyMapKeyEvent, Direction> entry : this.keys.entrySet()) {
            final KeyMapKeyEvent keyEvent = entry.getKey();
            if (keyEvent.key().equalsKey(searchKey)) return true;
        }
        return false;
    }

    @Nullable
    public KeyMapKeyEvent getKeyMapEvent(IKey searchKey) {
        for (Map.Entry<KeyMapKeyEvent, Direction> entry : this.keys.entrySet()) {
            final KeyMapKeyEvent keyEvent = entry.getKey();
            if (keyEvent.key().equalsKey(searchKey)) return keyEvent;
        }
        return null;
    }

    @Nullable
    public Direction getKeyDirection(IKey searchKey) {
        for (Map.Entry<KeyMapKeyEvent, Direction> entry : this.keys.entrySet()) {
            final KeyMapKeyEvent keyEvent = entry.getKey();
            if (keyEvent.key().equalsKey(searchKey)) return entry.getValue();
        }
        return null;
    }

    public Direction getCurrentDirection() {
        return this.currentDirection;
    }

    @Override
    public boolean isKeyDown(IKey key) {
        return this.getKeyMapEvent(key) != null;
    }
}
