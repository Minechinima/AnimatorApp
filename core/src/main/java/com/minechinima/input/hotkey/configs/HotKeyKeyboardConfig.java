package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.HotKeyConfig;
import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnum;
import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnumValue;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UIEnum;

public class HotKeyKeyboardConfig extends AbstractHotKeyKeyConfig {
    @InGui(as = UIEnum.class, translationPrefix = "key")
    public final KeyboardKeyEnumValue key = new KeyboardKeyEnumValue("key", KeyboardKeyEnum.KEY_A);

    public HotKeyKeyboardConfig(String title, String tooltip) {
        super(title, tooltip);
        this.addChild("key", this.key);
    }

    @Override
    public boolean onEvent(KeyMapEvent event) {
        return super.onEvent(event)
                && event instanceof KeyMapKeyEvent keyEvent
                && this.key.get().equalsRawKey(keyEvent.key());
    }
}
