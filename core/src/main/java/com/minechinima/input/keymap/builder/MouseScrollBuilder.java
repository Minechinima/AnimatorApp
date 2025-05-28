package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.events.KeyMapScrollEvent;
import com.minechinima.input.raw.ScrollEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class MouseScrollBuilder extends AbstractKeyMapEventBuilder<ScrollEvent, KeyMapScrollEvent> {
    @Override
    public @Nullable Collection<KeyMapScrollEvent> getResult() {
        return this.isArgNull() ? null : List.of(new KeyMapScrollEvent((float) this.event.getXOffset(),
                (float) this.event.getYOffset(),
                this.mouseX,
                this.mouseY,
                this.window,
                this.pressedKeySet));
    }
}
