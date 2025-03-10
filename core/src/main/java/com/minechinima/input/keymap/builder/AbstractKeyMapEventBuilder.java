package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.raw.InputEvent;
import com.minechinima.ui.elements.UIWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKeyMapEventBuilder<T extends InputEvent, J extends KeyMapEvent> implements IKeyMapEventBuilder<T, J> {
    @Nullable
    protected Double mouseX;
    @Nullable
    protected Double mouseY;
    @Nullable
    protected UIWindow window;
    @Nullable
    protected PressedKeySet pressedKeySet;
    @Nullable
    protected T event;

    @Override
    public void setMousePos(double prevMouseX, double prevMouseY, double currentMouseX, double currentMouseY) {
        this.mouseX = currentMouseX;
        this.mouseY = currentMouseY;
    }

    @Override
    public void setUIWindow(@NotNull UIWindow window) {
        this.window = window;
    }

    @Override
    public void setPressedKeys(@NotNull PressedKeySet pressedKeys) {
        this.pressedKeySet = pressedKeys;
    }

    /**
     * @return true when one of the arguments is null.
     */
    protected boolean isArgNull() {
        return this.mouseX == null || this.mouseY == null || this.window == null || this.pressedKeySet == null;
    }

    @Override
    public void reset() {
        this.mouseX = null;
        this.mouseY = null;
        this.window = null;
        this.pressedKeySet = null;
        this.event = null;
    }

    @Override
    public void onEvent(T event) {
        this.event = event;
    }
}
