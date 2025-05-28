package com.minechinima.operator;

import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.ui.utils.ILangKeyProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Command that modifies the properties of an Operator on a key map event.<br>
 * Example use case: Modal Operator to move an object -> while moving you can press X to only move on the X axis. This will
 * call this class which will set the corresponding property.
 * <br><br>
 * The command methods are executed on the configuration instances, YOU CANNOT STORE RUNTIME STATES HERE AS THESE INSTANCES ARE REUSED.
 * @param <P>
 */
public abstract class PropertyModifierGroupStruct<P extends OperatorPropertiesGroupStruct> extends ValueGroup implements ILangKeyProvider {
    private final String title;
    private final String description;

    public PropertyModifierGroupStruct(String title, String description) {
        super("");
        this.title = title;
        this.description = description;
    }


    public abstract void onEvent(P originalProps, P props);

    /**
     * See {@link HotKeyConfigGroupStruct#isStopEvent(KeyMapEvent)}<br>
     * Can be used to implement toggle modifier that undo the action when the key is released.
     * <br><br>
     * Might not always be called!
     */
    public abstract void onStopEvent(P originalProps, P props);

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public @Nullable String tooltip() {
        return this.description;
    }
}
