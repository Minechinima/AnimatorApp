package com.minechinima.input.keymap;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

/**
 * For a {@link IKeyMapEventBuilder} that can provide a mouse position from the event it is converting.
 */
public interface IMousePosEventProvider {
    /**
     * @return null if the event was not set.
     */
    @Nullable
    Vector2d getMousePos();
}
