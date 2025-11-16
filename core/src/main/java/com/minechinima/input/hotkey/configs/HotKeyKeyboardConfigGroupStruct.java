package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.configs.values.KeyType;
import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnum;
import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnumValue;
import org.jetbrains.annotations.Nullable;

public class HotKeyKeyboardConfigGroupStruct extends AbstractHotKeyKeyConfigGroupStruct {
    public HotKeyKeyboardConfigGroupStruct(String title, String tooltip) {
        super(title, tooltip, new KeyboardKeyEnumValue("key", KeyboardKeyEnum.KEY_A));
    }

    @Override
    public @Nullable HotKeyConfigGroupStruct getReleaseConfig() {
        if (this.type.get() == KeyType.DOUBLECLICK || this.type.get() == KeyType.CLICK) return null;

        HotKeyKeyboardConfigGroupStruct cfg = new HotKeyKeyboardConfigGroupStruct(this.title(), this.tooltip());
        cfg.key.set(this.key.get());
        if (this.type.get() == KeyType.RELEASE) {
            cfg.type.set(KeyType.PRESS);
        } else {
            cfg.type.set(KeyType.RELEASE);
        }

        return cfg;
    }
}
