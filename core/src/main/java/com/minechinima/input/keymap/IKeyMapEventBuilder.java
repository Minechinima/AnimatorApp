package com.minechinima.input.keymap;

import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.raw.IInputEventListener;
import com.minechinima.input.raw.InputEvent;
import com.minechinima.ui.elements.UIWindow;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * A builder for converting an {@link InputEvent} (received via {@link IInputEventListener#onEvent(InputEvent)}) into a {@link KeyMapEvent}.
 * <br><br>
 * This allows for a dynamic step by step build of a keymapevent. This allows the KeyMapEventManager to have some control over
 * KeyMapEvent aspects without exposing concrete event classes.
 * @param <I> input class, ideally a record, with the event types this builder requires
 * @param <O> the output keymap event.
 */
public interface IKeyMapEventBuilder<I extends InputEvent, O extends KeyMapEvent> {
    void setInputEvent(I inputs);
    void setMousePos(double prevMouseX, double prevMouseY, double currentMouseX, double currentMouseY);
    void setUIWindow(@NotNull UIWindow window);
    void setPressedKeys(@NotNull PressedKeySet pressedKeys);

    /**
     * @return the result of the builder or null if the builder was not completely setup. Can be multiple events too.
     */
    @Nullable
    O getResult();

    /**
     * Reset the builder parameters.
     */
    void reset();
}
