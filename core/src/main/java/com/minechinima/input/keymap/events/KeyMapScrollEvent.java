package com.minechinima.input.keymap.events;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.ui.elements.window.UIWindow;

public class KeyMapScrollEvent extends KeyMapEvent {
    private final float xOffset;
    private final float yOffset;

    public KeyMapScrollEvent(float xOffset, float yOffset, double mouseX, double mouseY, UIWindow window, PressedKeySet pressedKeys) {
        super(mouseX, mouseY, window, pressedKeys);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public float getxOffset() {
        return this.xOffset;
    }

    public float getyOffset() {
        return this.yOffset;
    }
}
