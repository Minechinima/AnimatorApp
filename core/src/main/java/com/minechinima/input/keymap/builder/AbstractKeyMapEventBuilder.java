package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.IKeyMapEventBuilder;
import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.raw.InputEvent;
import com.minechinima.ui.elements.UIWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKeyMapEventBuilder<I extends InputEvent, J extends KeyMapEvent> implements IKeyMapEventBuilder<I, J> {
    @Nullable
    protected Double mouseX;
    @Nullable
    protected Double mouseY;
    @Nullable
    protected Double prevMouseX;
    @Nullable
    protected Double prevMouseY;
    @Nullable
    protected PressedKeySet pressedKeySet;
    @Nullable
    protected I event;
    @Nullable
    protected UIWindow window;

    @Override
    public void setInputEvent(I inputs) {
        this.event = inputs;
    }

    @Override
    public void setUIWindow(UIWindow window) {
        this.window = window;
    }

    @Override
    public void setMousePos(double prevMouseX, double prevMouseY, double currentMouseX, double currentMouseY) {
        this.mouseX = currentMouseX;
        this.mouseY = currentMouseY;
        this.prevMouseX = prevMouseX;
        this.prevMouseY = prevMouseY;
    }

    @Override
    public void setPressedKeys(@NotNull PressedKeySet pressedKeys) {
        this.pressedKeySet = pressedKeys;
    }

    /**
     * @return true when one of the arguments is null.
     */
    protected boolean isArgNull() {
        return this.mouseX == null || this.mouseY == null
                || this.prevMouseX == null || this.prevMouseY == null
                || this.pressedKeySet == null || this.window == null;
    }

    @Override
    public void reset() {
        this.mouseX = null;
        this.mouseY = null;
        this.prevMouseX = null;
        this.prevMouseY = null;
        this.pressedKeySet = null;
        this.event = null;
        this.window = null;
    }
}
