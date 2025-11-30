package com.minechinima.input.hotkey;

import com.minechinima.input.hotkey.configs.AbstractHotKeyKeyConfigGroupStruct;
import com.minechinima.input.hotkey.configs.KeyModifiers;
import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnum;
import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnumValue;
import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyType;
import com.minechinima.input.raw.Key;
import com.minechinima.model.values.AbstractEnumValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeyModifiersTest {
    @Test
    public void testNoModifiersPressedWithSetModifier() {
        AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> key = (AbstractEnumValue) new KeyboardKeyEnumValue("key");
        KeyModifiers modifiers = new KeyModifiers("test", key);
        modifiers.initChildren();
        modifiers.ctrl.set(true);

        PressedKeySet pressedKeySet = new PressedKeySet();


        Assertions.assertFalse(modifiers.isModPressed(pressedKeySet));
    }

    @Test
    public void testModifierPressedWithNoSetModifier() {
        AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> key = (AbstractEnumValue) new KeyboardKeyEnumValue("key");
        KeyModifiers modifiers = new KeyModifiers("test", key);
        modifiers.initChildren();

        PressedKeySet pressedKeySet = new PressedKeySet();
        pressedKeySet.add(new KeyMapKeyEvent(Key.KEY_LEFT_CONTROL, KeyType.PRESS, 0, 0, null, pressedKeySet));

        Assertions.assertFalse(modifiers.isModPressed(pressedKeySet));
    }

    @Test
    public void testMultipleModifiersPressedWithSetSingleModifier() {
        AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> key = (AbstractEnumValue) new KeyboardKeyEnumValue("key");
        KeyModifiers modifiers = new KeyModifiers("test", key);
        modifiers.initChildren();
        modifiers.shift.set(true);

        PressedKeySet pressedKeySet = new PressedKeySet();
        pressedKeySet.add(new KeyMapKeyEvent(Key.KEY_LEFT_CONTROL, KeyType.PRESS, 0, 0, null, pressedKeySet));
        pressedKeySet.add(new KeyMapKeyEvent(Key.KEY_LEFT_SHIFT, KeyType.PRESS, 0, 0, null, pressedKeySet));

        Assertions.assertFalse(modifiers.isModPressed(pressedKeySet));
    }

    @Test
    public void testModifierPressedWithMatchingSetModifier() {
        AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> key = (AbstractEnumValue) new KeyboardKeyEnumValue("key");
        KeyModifiers modifiers = new KeyModifiers("test", key);
        modifiers.initChildren();
        modifiers.ctrl.set(true);

        PressedKeySet pressedKeySet = new PressedKeySet();
        pressedKeySet.add(new KeyMapKeyEvent(Key.KEY_LEFT_CONTROL, KeyType.PRESS, 0, 0, null, pressedKeySet));

        Assertions.assertTrue(modifiers.isModPressed(pressedKeySet));
    }

    @Test
    public void testMultipleModifierPressedWithMatchingSetModifiers() {
        AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> key = (AbstractEnumValue) new KeyboardKeyEnumValue("key");
        KeyModifiers modifiers = new KeyModifiers("test", key);
        modifiers.initChildren();
        modifiers.ctrl.set(true);
        modifiers.shift.set(true);

        PressedKeySet pressedKeySet = new PressedKeySet();
        pressedKeySet.add(new KeyMapKeyEvent(Key.KEY_LEFT_CONTROL, KeyType.PRESS, 0, 0, null, pressedKeySet));
        pressedKeySet.add(new KeyMapKeyEvent(Key.KEY_LEFT_SHIFT, KeyType.PRESS, 0, 0, null, pressedKeySet));

        Assertions.assertTrue(modifiers.isModPressed(pressedKeySet));
    }

    @Test
    public void testModifierPressedWithNoSetModifierButMatchingKey() {
        AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> key = (AbstractEnumValue) new KeyboardKeyEnumValue("key");
        key.set(KeyboardKeyEnum.KEY_LEFT_CONTROL);
        KeyModifiers modifiers = new KeyModifiers("test", key);
        modifiers.initChildren();

        PressedKeySet pressedKeySet = new PressedKeySet();
        pressedKeySet.add(new KeyMapKeyEvent(Key.KEY_LEFT_CONTROL, KeyType.PRESS, 0, 0, null, pressedKeySet));

        Assertions.assertTrue(modifiers.isModPressed(pressedKeySet));
    }
}
