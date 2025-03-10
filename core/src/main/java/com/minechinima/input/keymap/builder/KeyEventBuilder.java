package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyType;
import com.minechinima.input.raw.InputType;
import com.minechinima.input.raw.KeyEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class KeyEventBuilder extends AbstractKeyMapEventBuilder<KeyEvent, KeyMapKeyEvent> implements IKeyEventBuilder<KeyEvent, KeyMapKeyEvent> {
    protected KeyType type;

    @Override
    protected boolean isArgNull() {
        return super.isArgNull() || this.type == null;
    }

    @Override
    public void reset() {
        super.reset();
        this.type = null;
    }

    @Override
    public @Nullable InputType getInputType() {
        if (this.event == null) return null;
        return this.event.getInputType();
    }

    @Override
    public void setKeyMapType(KeyType type) {
        this.type = type;
    }

    @Override
    public @Nullable Collection<KeyMapKeyEvent> getResult() {
        if (this.isArgNull()) return null;
        List<KeyMapKeyEvent> list = new LinkedList<>();
        list.add(new KeyMapKeyEvent(this.event.getCurrentKey(), this.type, this.mouseX, this.mouseY, this.window, this.pressedKeySet));
        return list;
    }
}
