package com.minechinima.operator.config.modal;

import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;

public class ModalFunctionConfigGroupStruct<C extends IContext> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final ModalFunctionSelectorEnumStruct<C> modifier;

    public ModalFunctionConfigGroupStruct(ModalFunctionConfigsMapStruct<C> category) {
        super("");
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", category.hotKeyConfigRegistry));
        this.addChild("modifier_config", this.modifier = new ModalFunctionSelectorEnumStruct<>("modifier_config", category,null));
    }
}
