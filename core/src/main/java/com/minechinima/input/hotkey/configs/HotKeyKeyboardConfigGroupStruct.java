package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnum;
import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnumValue;

public class HotKeyKeyboardConfigGroupStruct extends AbstractHotKeyKeyConfigGroupStruct {
    public HotKeyKeyboardConfigGroupStruct(String title, String tooltip) {
        super(title, tooltip, new KeyboardKeyEnumValue("key", KeyboardKeyEnum.KEY_A));
    }
}
