package com.minechinima.input.hotkey.configs;

import com.minechinima.input.hotkey.configs.values.KeyModEnum;
import com.minechinima.input.keymap.PressedKeySet;
import com.minechinima.input.raw.IKey;
import com.minechinima.model.events.types.AbstractEvent;
import com.minechinima.model.events.types.ChangeEvent;
import com.minechinima.model.values.AbstractEnumValue;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.utils.Pair;

import java.util.HashSet;
import java.util.Set;

public class KeyModifiers extends ValueGroup {
    @InGui(as = UICheckBox.class, translationPrefix = "any")
    public final BooleanValue any = new BooleanValue("any");
    @InGui(as = UICheckBox.class, translationPrefix = "shift")
    public final BooleanValue shift = new BooleanValue("shift");
    @InGui(as = UICheckBox.class, translationPrefix = "ctrl")
    public final BooleanValue ctrl = new BooleanValue("ctrl");
    @InGui(as = UICheckBox.class, translationPrefix = "alt")
    public final BooleanValue alt = new BooleanValue("alt");

    private final AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> keyToModify;

    /**
     * Set of the booleans for their key modifiers for dynamic lookup if the
     * modifier key is pressed. {@link #isModPressed(PressedKeySet)}
     */
    protected final Set<Pair<IKey, BooleanValue>> keyComparisons = new HashSet<>();

    public KeyModifiers(String id, AbstractEnumValue<AbstractHotKeyKeyConfigGroupStruct.IKeyEnum> keyToModify) {
        super(id);
        this.keyToModify = keyToModify;
        this.addChild("any", this.any);
        this.addChild("shift", this.shift);
        this.addChild("ctrl", this.ctrl);
        this.addChild("alt", this.alt);

        this.keyComparisons.add(new Pair<>(KeyModEnum.SHIFT, this.shift));
        this.keyComparisons.add(new Pair<>(KeyModEnum.ALT, this.alt));
        this.keyComparisons.add(new Pair<>(KeyModEnum.CONTROL, this.ctrl));
    }

    @Override
    protected void onChildEvent(AbstractEvent event) {
        if (!(event instanceof ChangeEvent<?> changeEvent)) return;

        if (changeEvent.getTrigger() == this.any) {
            if (this.any.get()) {
                this.keyComparisons.forEach(p -> p.getB().set(true));
            }
        } else if (changeEvent.getTrigger() instanceof BooleanValue booleanValue) {
            if (!booleanValue.get()) {
                this.any.set(false);
            }
        }
    }

    public boolean isModPressed(PressedKeySet pressed) {
        if (this.any.get()) {
            return true;
        } else {
            /**
             * Go through the available modifiers and check for the activated
             * modifiers if they are pressed. If an activated modifier is not pressed
             * and is not the main key {@link #keyToModify) (main key is managed elsewhere) return false.
             */
            for (Pair<IKey, BooleanValue> config : this.keyComparisons) {
                boolean isModifierEnabled = config.getB().get();
                boolean isModifierPressed = pressed.getEventByKey(config.getA()) != null;

                if (config.getA().equalsKey(this.keyToModify.get())) continue;

                if (isModifierEnabled ^ isModifierPressed) {
                    return false;
                }
            }
        }

        return true;
    }
}
