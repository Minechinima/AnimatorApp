package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.HotKeyConfig;
import com.minechinima.input.hotkey.configs.values.MouseKeyEnum;
import com.minechinima.input.hotkey.configs.values.MouseKeyEnumValue;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UIEnum;

public class HotKeyMouseConfig extends AbstractHotKeyKeyConfig {
    @InGui(as = UIEnum.class, translationPrefix = "Key")
    public final MouseKeyEnumValue key = new MouseKeyEnumValue("key", MouseKeyEnum.MOUSE_BUTTON_1);

    public HotKeyMouseConfig(String title, String tooltip) {
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
