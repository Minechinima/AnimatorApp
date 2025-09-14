package com.minechinima.operator.config.modal;

import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.OperatorPropertiesGroupStruct;

/**
 * Configuration for a {@link com.minechinima.operator.PropertyModifierGroupStruct}
 * @param <P>
 */
public class PropertyModifierConfigGroupStruct<P extends OperatorPropertiesGroupStruct> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final PropertyModifierSelectorEnumStruct<P> modifier;

    public PropertyModifierConfigGroupStruct(PropertyModifiersListStruct<P> category) {
        super("");
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", category.hotKeyConfigRegistry));
        this.addChild("modifier_config", this.modifier = new PropertyModifierSelectorEnumStruct<>("modifier_config", category,null));
    }
}
