package com.minechinima.operator;

import com.minechinima.input.keymap.events.KeyMapEvent;

/**
 * Interface for a strategy whether a given keymap event should be blocked and not trigger an operator.
 * <br><br>
 * This is used to prevent keys that are held down while an operator is executed to immediately trigger a new operator.
 * The user should first release the key.
 * @param <T> type of the cached event.
 */
public interface ICachedEventBlocker<T extends KeyMapEvent> {
    /**
     * @param cache
     * @return true if the event should be cached and used for next events to block.
     */
    boolean cacheEvent(T cache);

    /**
     * @param cached
     * @param triggered
     * @return true if the triggered event should not trigger a new operator
     */
    boolean isEventBlocked(T cached, KeyMapEvent triggered);

    /**
     * @param triggered
     * @return true if the cached event should be cleared from the cache if the triggered event arrives.
     */
    boolean isEventCleared(T cached, KeyMapEvent triggered);
}
