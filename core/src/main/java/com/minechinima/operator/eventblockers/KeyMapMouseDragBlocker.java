package com.minechinima.operator.eventblockers;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.keymap.events.*;

import java.util.Map;

public class KeyMapMouseDragBlocker implements ICachedEventBlocker<KeyMapMouseDragEvent> {
    private final PressedKeySet pressedKeys = new PressedKeySet();
    @Override
    public boolean cacheEvent(KeyMapMouseDragEvent cache) {
        boolean firstTimeCaching = this.pressedKeys.isEmpty();
        Map<KeyMapKeyEvent, KeyMapMouseMoveEvent.Direction> draggedKeys = cache.getDraggedKeys();
        if (draggedKeys.isEmpty()) return false;
        /* cache the pressed keys but only cache one DragKeyEvent as we build our own cache */
        this.pressedKeys.addAll(draggedKeys.keySet());
        return firstTimeCaching;
    }

    @Override
    public boolean isEventBlocked(KeyMapMouseDragEvent cached, KeyMapEvent triggered) {
        if (!(triggered instanceof IKeyMapKeyEvent keyEvent)) return false;
        /* check if any of the pressed keys of the previous dragevents is pressed in the triggered event */
        for (KeyMapKeyEvent key : this.pressedKeys) {
            if (keyEvent.isKeyDown(key.key())) return true;
        }
        return false;
    }

    @Override
    public boolean clearCachedEvent(KeyMapMouseDragEvent cached, KeyMapEvent triggered) {
        if (!(triggered instanceof KeyMapKeyEvent keyEvent)) return false;
        if (keyEvent.type() == KeyType.RELEASE) {
            this.pressedKeys.removeEqualKeys(keyEvent);
        }
        return this.pressedKeys.isEmpty();
    }
}
