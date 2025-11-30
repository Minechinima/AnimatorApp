package com.minechinima.input.keymap.events;

import com.minechinima.input.raw.IKey;

/**
 * Interface for events that are about a key to check if the key is down in this event.<br><br>
 * <b>Important</b>: this is not about the global pressed keys but the keys that this event targets e.g. mouse drag
 * has only keys down that exceeded the drag threshold
 */
public interface IKeyMapKeyEvent {
    boolean isKeyDown(IKey key);
}
