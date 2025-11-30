package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.configs.values.KeyType;
import com.minechinima.input.hotkey.configs.values.MouseKeyEnum;
import com.minechinima.input.hotkey.configs.values.MouseKeyEnumValue;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapScrollEvent;
import org.jetbrains.annotations.Nullable;

import static com.minechinima.input.hotkey.configs.values.MouseKeyEnum.MOUSE_WHEEL_DOWN;
import static com.minechinima.input.hotkey.configs.values.MouseKeyEnum.MOUSE_WHEEL_UP;

public class HotKeyMouseConfigGroupStruct extends AbstractHotKeyKeyConfigGroupStruct {
    public HotKeyMouseConfigGroupStruct(String title, String tooltip) {
        super(title, tooltip, new MouseKeyEnumValue("key", MouseKeyEnum.MOUSE_BUTTON_1));
    }

    @Override
    public boolean onEvent(KeyMapEvent event) {
        MouseKeyEnum key = (MouseKeyEnum) this.key.get();
        if (key == MOUSE_WHEEL_UP || key == MouseKeyEnum.MOUSE_WHEEL_DOWN) {
            if (!(event instanceof KeyMapScrollEvent scrollEvent)) return false;
            return this.mod.isModPressed(event.getPressedKeys())
                    && (scrollEvent.getyOffset() > 0 && key == MOUSE_WHEEL_UP)
                    || (scrollEvent.getyOffset() < 0 && key == MouseKeyEnum.MOUSE_WHEEL_DOWN);
        } else {
            return super.onEvent(event);
        }
    }

    @Override
    public @Nullable HotKeyConfigGroupStruct getReleaseConfig() {
        if (this.type.get() == KeyType.DOUBLECLICK || this.type.get() == KeyType.CLICK) return null;
        if (this.key.get() == MOUSE_WHEEL_UP || this.key.get() == MOUSE_WHEEL_DOWN) return null;

        HotKeyMouseConfigGroupStruct cfg = new HotKeyMouseConfigGroupStruct(this.title(), this.tooltip());
        cfg.key.set(this.key.get());
        cfg.mod.any.set(true);
        if (this.type.get() == KeyType.RELEASE) {
            cfg.type.set(KeyType.PRESS);
        } else {
            cfg.type.set(KeyType.RELEASE);
        }

        return cfg;
    }
}
