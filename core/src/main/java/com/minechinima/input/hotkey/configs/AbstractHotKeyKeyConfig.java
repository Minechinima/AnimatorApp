package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.*;
import com.minechinima.input.hotkey.configs.values.KeyModEnum;
import com.minechinima.input.hotkey.configs.values.KeyModEnumValue;
import com.minechinima.input.hotkey.configs.values.KeyType;
import com.minechinima.input.hotkey.configs.values.KeyTypeEnumValue;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.raw.Key;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UIEnum;

public abstract class AbstractHotKeyKeyConfig extends HotKeyConfig {
    @InGui(as = UIEnum.class, translationPrefix = "type")
    public final KeyTypeEnumValue type = new KeyTypeEnumValue("type", KeyType.PRESS);
    @InGui(as = UIEnum.class, translationPrefix = "mod")
    public final KeyModEnumValue mod = new KeyModEnumValue("mod", null);
    //TODO key mod

    public AbstractHotKeyKeyConfig(String title, String tooltip) {
        super(title, tooltip);
        this.addChild("type", this.type);
        this.addChild("mod", this.mod);
    }

    @Override
    public boolean onEvent(KeyMapEvent event) {
        if (!(event instanceof KeyMapKeyEvent keyEvent)) return false;
        if (!this.type.get().equalsKeyType(keyEvent.type())) return false;

        if (this.mod.get() != null) {
            boolean modPressed = false;
            for (Key key : this.mod.get().keys) {
                if (event.getPressedKeys().getEventByKey(key) != null) {
                    modPressed = true;
                }
            }
            if (!modPressed) return false;
        }

        return true;
    }


    @Override
    public boolean isStopEvent(KeyMapEvent event) {
        if (!(event instanceof KeyMapKeyEvent keyEvent)) return false;

        switch (this.type.get()) {
            case PRESS, REPEAT, CLICK, DOUBLECLICK -> {
                return keyEvent.type() == com.minechinima.input.keymap.events.KeyType.RELEASE;
            } case RELEASE -> {
                return keyEvent.type() == com.minechinima.input.keymap.events.KeyType.PRESS;
            }
        }
        return false;
    }
}
