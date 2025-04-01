package com.minechinima.input.hotkey;

import com.minechinima.input.keymap.IKeyMapEventListener;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.ui.utils.ILangKeyProvider;

//TODO we need to decide on some kind of suffix or prefix for class names that are subclasses of Base data stuff and are put into the data tree....
// it will get very confusing when everything is such a data class. Maybe even a special suffix for ImmutableHierarchy so we do not forget to initialize (it happens often)

/**
 * The actual configuration of a hotkey. This class also contains the logic when to respond to a {@link KeyMapEvent} as it
 * has the data to configure that process.
 */
public abstract class HotKeyConfig extends ValueGroup implements ILangKeyProvider, IKeyMapEventListener<KeyMapEvent> {
    /* for UI */
    private final String title;
    private final String tooltip;

    public HotKeyConfig(String title, String tooltip) {
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
     * This method tests whether the event is the "counterpart" of the event that would trigger this configuration.
     * <br><br>
     * Example use case: an event triggers a continuous user action (e.g. pressing a key), this method can then define when
     * to stop that continuous user action (e.g. releasing the key)
     * @param event
     * @return true if the event is a stop event for this configuration.
     */
    public abstract boolean isStopEvent(KeyMapEvent event);

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public String tooltip() {
        return this.tooltip;
    }
}
