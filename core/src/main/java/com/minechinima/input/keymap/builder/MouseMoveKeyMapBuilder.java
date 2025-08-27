package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.IMousePosEventProvider;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.input.raw.MouseEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

public class MouseMoveKeyMapBuilder extends AbstractKeyMapEventBuilder<MouseEvent, KeyMapMouseMoveEvent> implements IMousePosEventProvider {
    @Override
    public @Nullable KeyMapMouseMoveEvent getResult() {
        if (this.isArgNull()) return null;

        return new KeyMapMouseMoveEvent(this.mouseX, this.mouseY, this.prevMouseX, this.prevMouseY, this.window, this.pressedKeySet);
    }

    @Override
    public Vector2d getMousePos() {
        return this.event == null ? null : new Vector2d(this.event.getX(), this.event.getY());
    }
}
