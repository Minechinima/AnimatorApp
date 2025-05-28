package com.minechinima.operator.config.modal;

import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.OperatorPropertiesGroupStruct;

public class PropModifierKeyConfigItemGroupStruct<P extends OperatorPropertiesGroupStruct> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final PropertyModifierSelectorEnumStruct<P> modifier;

    public PropModifierKeyConfigItemGroupStruct(PropModifierKeyConfigListStruct<P> category) {
        super("");
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", category.hotKeyConfigRegistry));
        this.addChild("modifier_config", this.modifier = new PropertyModifierSelectorEnumStruct<>("modifier_config", category,null));
    }
}
