package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.raw.IInputEventListener;
import com.minechinima.input.raw.InputEvent;
import com.minechinima.ui.elements.UIWindow;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A builder for converting an {@link InputEvent} (received via {@link IInputEventListener#onEvent(InputEvent)}) into a {@link KeyMapEvent}.
 * <br><br>
 * This allows for a dynamic step by step build of a keymapevent. This allows the KeyMapEventManager to have some control over
 * KeyMapEvent aspects without exposing concrete event classes.
 * @param <T>
 * @param <J>
 */
public interface IKeyMapEventBuilder<T extends InputEvent, J extends KeyMapEvent> extends IInputEventListener<T> {
    void setMousePos(double prevMouseX, double prevMouseY, double currentMouseX, double currentMouseY);
    void setUIWindow(@NotNull UIWindow window);
    void setPressedKeys(@NotNull PressedKeySet pressedKeys);

    /**
     * @return the result of the builder or null if the builder was not completely setup. Can be multiple events too.
     */
    @Nullable
    Collection<J> getResult();

    /**
     * Reset the builder parameters.
     */
    void reset();
}
