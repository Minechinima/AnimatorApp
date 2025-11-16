package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.*;
import com.minechinima.input.hotkey.configs.values.KeyModEnumValue;
import com.minechinima.input.hotkey.configs.values.KeyType;
import com.minechinima.input.hotkey.configs.values.KeyTypeEnumValue;
import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyMapMouseDragEvent;
import com.minechinima.input.raw.IKey;
import com.minechinima.input.raw.Key;
import com.minechinima.model.values.AbstractEnumValue;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIEnum;
import com.minechinima.ui.utils.ILangKeyProvider;

/**
 * Template for hotkey configs that are about a key.
 */
public abstract class AbstractHotKeyKeyConfigGroupStruct extends HotKeyConfigGroupStruct {
    @InGui(as = UIEnum.class, translationPrefix = "type")
    public final KeyTypeEnumValue type = new KeyTypeEnumValue("type", KeyType.PRESS);
    @InGui(as = UIEnum.class, translationPrefix = "mod")
    public final KeyModEnumValue mod = new KeyModEnumValue("mod", null);
    @InGui(as = UIEnum.class, translationPrefix = "key")
    public final AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> key;

    public <K extends AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> AbstractHotKeyKeyConfigGroupStruct(String title, String tooltip, AbstractEnumValue<K> key) {
        super(title, tooltip);
        this.addChild("type", this.type);
        this.addChild("mod", this.mod);
        this.addChild("key", this.key = (AbstractEnumValue<IKeyEnum>) key);
    }

    @Override
    public boolean onEvent(KeyMapEvent event) {
        Key keyEvent;
        boolean typeEqual;
        if (this.type.get() == KeyType.CLICKDRAG && event instanceof KeyMapMouseDragEvent dragEvent) {
            keyEvent = dragEvent.key();
            typeEqual = true;
        } else if (event instanceof KeyMapKeyEvent keyEvent1) {
            typeEqual = this.type.get().equalsKeyType(keyEvent1.type()) && keyEvent1.key().equalsKey(this.key.get());
        } else {
            return false;
        }

        return this.isModPressed(event.getPressedKeys())
                && typeEqual;
    }

    protected boolean isModPressed(PressedKeySet pressed) {
        if (this.mod.get() != null) {
            boolean modPressed = false;
            for (Key key : this.mod.get().keys) {
                if (pressed.getEventByKey(key) != null) {
                    modPressed = true;
                }
            }

            if (!modPressed) return false;
        }

        return true;
    }

    /**
     * interface for the key value so we can have a generic key enum here and common event checking logic.
     */
    public interface IKeyEnum extends IKey, ILangKeyProvider {
    }
}
