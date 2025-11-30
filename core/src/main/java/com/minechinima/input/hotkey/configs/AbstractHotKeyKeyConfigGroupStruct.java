package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.*;
import com.minechinima.input.hotkey.configs.values.KeyModEnum;
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
import com.minechinima.ui.elements.UIKeyModifiers;
import com.minechinima.ui.utils.ILangKeyProvider;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Template for hotkey configs that are about a key.
 */
public abstract class AbstractHotKeyKeyConfigGroupStruct extends HotKeyConfigGroupStruct {
    @InGui(as = UIEnum.class, translationPrefix = "type")
    public final KeyTypeEnumValue type = new KeyTypeEnumValue("type", KeyType.PRESS);
    //TODO add a feature that fetches the UIHandler so we can define complex sub datastructures without extra UI classes.
    @InGui(as = UIKeyModifiers.class, translationPrefix = "mod")
    public final KeyModifiers mod;
    @InGui(as = UIEnum.class, translationPrefix = "key")
    public final AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> key;

    public <K extends AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> AbstractHotKeyKeyConfigGroupStruct(String title, String tooltip, AbstractEnumValue<K> key) {
        super(title, tooltip);
        this.addChild("type", this.type);
        this.addChild("mod", this.mod = new KeyModifiers("mod", (AbstractEnumValue<IKeyEnum>) key));
        this.addChild("key", this.key = (AbstractEnumValue<IKeyEnum>) key);
        this.mod.initChildren(); //TODO bug was here: children not init -> not part of tree -> no event when changing value -> any=true did not set others to true.
    }

    @Override
    public boolean onEvent(KeyMapEvent event) {
        boolean keyEqual;
        if (this.type.get() == KeyType.CLICKDRAG && event instanceof KeyMapMouseDragEvent dragEvent) {
            keyEqual = dragEvent.isKeyDragged(this.key.get());
        } else if (event instanceof KeyMapKeyEvent keyEvent1) {
            keyEqual = this.type.get().equalsKeyType(keyEvent1.type()) && keyEvent1.key().equalsKey(this.key.get());
        } else {
            return false;
        }

        return this.mod.isModPressed(event.getPressedKeys())
                && keyEqual;
    }

    /**
     * interface for the key value so we can have a generic key enum here and common event checking logic.
     */
    public interface IKeyEnum extends IKey, ILangKeyProvider {
    }
}
