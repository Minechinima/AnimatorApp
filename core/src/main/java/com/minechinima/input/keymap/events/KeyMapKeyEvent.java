package com.minechinima.input.keymap.events;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.raw.Key;
import com.minechinima.ui.elements.UIWindow;

import java.util.Objects;

public class KeyMapKeyEvent extends KeyMapEvent {
    private final Key key;
    private final KeyType type;

    public KeyMapKeyEvent(Key key, KeyType type, double mouseX, double mouseY, UIWindow window, PressedKeySet pressedKeys) {
        super(mouseX, mouseY, window, pressedKeys);
        this.key = key;
        this.type = type;
    }

    /**
     * @return
     * @implNote Do not use this value to compare if two events were fired by the same key, use {@link #equalKey(KeyMapKeyEvent)}
     */
    public Key key() {
        return this.key;
    }

    public KeyType type() {
        return this.type;
    }

    /**
     * @param event
     * @return true if the event was fired by the same key. The type (press,release...) is irrelevant here.
     */
    public boolean equalKey(KeyMapKeyEvent event) {
        return this.key == event.key;
    }
}
