package com.minechinima.input.keymap.events;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.raw.Key;
import com.minechinima.ui.elements.window.UIWindow;

public class KeyMapKeyEvent extends KeyMapEvent {
    /**
     * @implNote this is not used in the core framework of keymap stuff. Instead, the more general equalKey method is
     * used to allow future subclasses with custom keys and better runtime extensibility.
     */
    private final Key key;
    private final KeyType type;

    public KeyMapKeyEvent(Key key, KeyType type, double mouseX, double mouseY, UIWindow window, PressedKeySet pressedKeys) {
        super(mouseX, mouseY, window, pressedKeys);
        this.key = key;
        this.type = type;
    }

    public Key key() {
        return this.key;
    }

    public KeyType type() {
        return this.type;
    }
}
