package com.minechinima.input.hotkey;

import com.minechinima.input.keymap.IKeyMapEventListener;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.utils.ILangKeyProvider;

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
     * @return true if {@link #isStopEvent(KeyMapEvent)} should be used.
     * False if this hotkey configuration disabled the stop event tests or doesn't have the capabilities.
     */
    public abstract boolean useStopEvent();

    /**
     * If this configuration triggers a continuous action with an event, this method
     * defines when to stop the continuous action.<br><br>
     * This is different from the release event {@link #isReleaseEvent(KeyMapEvent)}.
     * <br><br>
     * Example: configuration triggers a modal operator when pressing and dragging a key, dragging a ui slider,
     * this method should return true for releasing the button.
     * @param event
     * @return true if the event is a stop event for this configuration.
     */
    public abstract boolean isStopEvent(KeyMapEvent event);

    /**
     * This method can be used to define whether this hotkey configuration has a release event
     * and whether to call {@link #isReleaseEvent(KeyMapEvent)}.<br>
     * If this returns false, the event (that originally triggered this) should not be blocked, as there are
     * no capabilities to check when the event is released / unblocked.<br><br>
     * See {@link #isReleaseEvent(KeyMapEvent)} for more information.
     * @return true if this configuration can respond to a release event with
     * {@link #isReleaseEvent(KeyMapEvent)} to unblock the config/event.
     */
    public abstract boolean hasReleaseEvent();

    /**
     * This method is for configurations that have a corresponding "counterpart"
     * or "release" for the event that triggered this configuration.<br>
     * Examples:<br>
     * press -> release | click-drag -> release | repeat -> release<br>
     * release -> release is not valid.<br>
     * click -> has no counterpart as it is fired after release
     * <br><br>
     * This method can be used to check when to stop blocking an event, e.g. when a key is still held down it is blocked
     * for other actions until it is released.
     * @param event
     * @return true if the given event is the counterpart of the event that would trigger this config, e.g. releasing a key.
     */
    public abstract boolean isReleaseEvent(KeyMapEvent event);

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public String tooltip() {
        return this.tooltip;
    }
}
