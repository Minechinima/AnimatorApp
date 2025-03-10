package com.minechinima.input.keymap;

import com.minechinima.input.keymap.events.KeyMapEvent;

public interface IKeyMapEventListener<E extends KeyMapEvent> {
    /**
     * @param event
     * @return true if the keymap event was consumed and should not be passed onto other listeners.
     */
    boolean onEvent(E event);
}
