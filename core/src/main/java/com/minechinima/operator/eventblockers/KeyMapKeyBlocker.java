package com.minechinima.operator.eventblockers;

import com.minechinima.input.keymap.events.IKeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyType;
import com.minechinima.operator.ICachedEventBlocker;

/**
 * Blocks all {@link IKeyMapKeyEvent} where the key is down of the cached key.
 */
public class KeyMapKeyBlocker implements ICachedEventBlocker<KeyMapKeyEvent> {
    @Override
    public boolean cacheEvent(KeyMapKeyEvent cache) {
        return cache.type() == KeyType.PRESS || cache.type() == KeyType.REPEAT;
    }

    @Override
    public boolean isEventBlocked(KeyMapKeyEvent cached, KeyMapEvent triggered) {
        return triggered instanceof IKeyMapKeyEvent keyEvent && keyEvent.isKeyDown(cached.key());
    }

    @Override
    public boolean clearCachedEvent(KeyMapKeyEvent cached, KeyMapEvent triggered) {
        return triggered instanceof KeyMapKeyEvent triggeredKeyEvent && triggeredKeyEvent.type() == KeyType.RELEASE
                && triggeredKeyEvent.key().equalsKey(cached.key());
    }
}
