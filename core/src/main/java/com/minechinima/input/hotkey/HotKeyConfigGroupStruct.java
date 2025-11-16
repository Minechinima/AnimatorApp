package com.minechinima.input.hotkey;

import com.minechinima.input.keymap.IKeyMapEventListener;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.ui.utils.ILangKeyProvider;
import org.jetbrains.annotations.Nullable;

/**
 * The actual configuration of a hotkey. This class also contains the logic when to respond to a {@link KeyMapEvent} as it
 * has the data to configure that process.
 */
public abstract class HotKeyConfigGroupStruct extends ValueGroup implements ILangKeyProvider, IKeyMapEventListener<KeyMapEvent> {
    /* for UI */
    private final String title;
    private final String tooltip;

    public HotKeyConfigGroupStruct(String title, String tooltip) {
        super("hotkey_config");
        this.title = title;
        this.tooltip = tooltip;
    }

    /**
     *
     * @param event
     * @return true if this hotkey configuration responds to the given event.
     */
    @Override
    public abstract boolean onEvent(KeyMapEvent event);

    /**
     * @return the hotkeyconfiguration with the equivalent settings where {@link #onEvent(KeyMapEvent)} is triggered
     *         with the release of this key.<br>
     *         Can be null if this hotkeyconfig does not support a "release" event.
     */
    @Nullable
    public abstract HotKeyConfigGroupStruct getReleaseConfig();

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public String tooltip() {
        return this.tooltip;
    }
}
