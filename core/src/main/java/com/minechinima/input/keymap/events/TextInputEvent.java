package com.minechinima.input.keymap.events;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.ui.elements.UIWindow;

import java.util.Objects;

public class TextInputEvent extends KeyMapEvent {
    private final char character;

    public TextInputEvent(char character, double mouseX, double mouseY, UIWindow window, PressedKeySet pressedKeySet) {
        super(mouseX, mouseY, window, pressedKeySet);
        this.character = character;
    }

    public char character() {
        return this.character;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TextInputEvent that)) return false;
        return character == that.character;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(character);
    }
}
