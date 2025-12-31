package com.minechinima.operator.eventblockers;

import com.minechinima.input.keymap.events.KeyMapEvent;

/**
 * Interface for a strategy whether a given keymap event should be blocked and not trigger an operator.
 * <br><br>
 * This is used to prevent keys that are held down while an operator is executed that would immediately trigger a new operator
 * when the old one is finished.
 * The user should first release the key.
 * <br><br>
 * The strategies are always called for every event that they marked with {@link #cacheEvent(KeyMapEvent)}.
 * Therefore this can be used as a state machine (to store their own cache variables).
 * @param <T> type of the cached event.
 */
public interface ICachedEventBlocker<T extends KeyMapEvent> {
    /**
     * @param cache
     * @return true if the event should be cached and used for next events to block.<br><br>
     *         If this returns true, this strategy will always be called for upcoming events
     *         for this cached event.<br><br>
     *         The strategy can cache multiple events,
     *         which will result in multiple {@link #isEventBlocked(KeyMapEvent, KeyMapEvent)} calls.
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
     * @return true if the cached event should be cleared from the cache and whether to stop calling this strategy,
     *         if it did not cache any other key.
     */
    boolean clearCachedEvent(T cached, KeyMapEvent triggered);
}
