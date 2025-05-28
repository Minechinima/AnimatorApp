package com.minechinima.input.keymap.events;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.ui.elements.UIWindow;

public abstract class KeyMapEvent {
    /* current mouse position relative to window coordinates. */
    private final double mouseX;
    private final double mouseY;
    /**
     * The pressed keys at the time of this event.
     * @implNote The KeyEvent instances are used to allow custom data storage for the pressed keys
     */
    private final PressedKeySet pressed = new PressedKeySet();
    /**
     * The ui window the event was triggered in.
     */
    private final UIWindow window;

    public KeyMapEvent(double mouseX, double mouseY, UIWindow window, PressedKeySet pressedKeys) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.window = window;
        this.pressed.addAll(pressedKeys);
    }

    public PressedKeySet getPressedKeys() {
        return new PressedKeySet(this.pressed);
    }

    public double mouseX() {
        return mouseX;
    }

    public double mouseY() {
        return mouseY;
    }

    public UIWindow window() {
        return window;
    }
}
