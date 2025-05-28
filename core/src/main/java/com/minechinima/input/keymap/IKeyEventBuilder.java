package com.minechinima.input.keymap;

import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyType;
import com.minechinima.input.raw.InputEvent;
import com.minechinima.input.raw.InputType;

import javax.annotation.Nullable;

/**
 * For an input event converter that can provide and input type for the event it is converting.
 * <br><br>
 * This is important for building click/double click events externally.
 * @param <T>
 * @param <J> should return a KeyMapKeyEvent as we need a key-event-type (press, release, double click...)
 */
public interface IKeyEventBuilder<T extends InputEvent, J extends KeyMapKeyEvent> extends IKeyMapEventBuilder<T, J> {
    /**
     * @return the input type of the event. Null if no event was passed.
     */
    @Nullable
    InputType getInputType();
    void setKeyMapType(KeyType type);
}
