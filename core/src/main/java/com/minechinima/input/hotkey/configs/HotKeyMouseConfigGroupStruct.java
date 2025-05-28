package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.configs.values.MouseKeyEnum;
import com.minechinima.input.hotkey.configs.values.MouseKeyEnumValue;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapScrollEvent;

public class HotKeyMouseConfigGroupStruct extends AbstractHotKeyKeyConfigGroupStruct {
    public HotKeyMouseConfigGroupStruct(String title, String tooltip) {
        super(title, tooltip, new MouseKeyEnumValue("key", MouseKeyEnum.MOUSE_BUTTON_1));
    }

    @Override
    public boolean onEvent(KeyMapEvent event) {
        //TODO get rid of abstract super class and generic shit
        MouseKeyEnum key = (MouseKeyEnum) this.key.get();
        if (key == MouseKeyEnum.MOUSE_WHEEL_UP || key == MouseKeyEnum.MOUSE_WHEEL_DOWN) {
            if (!(event instanceof KeyMapScrollEvent scrollEvent)) return false;
            return (scrollEvent.getyOffset() > 0 && key == MouseKeyEnum.MOUSE_WHEEL_UP)
                    || (scrollEvent.getyOffset() < 0 && key == MouseKeyEnum.MOUSE_WHEEL_DOWN);
        } else {
            return super.onEvent(event);
        }
    }
}
