package com.minechinima.operator.config.modal;

import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;

public class ModalConfigurationsGroupStruct<C extends IContext> extends ValueGroup {
    public final ModalFuncKeyConfigListStruct<C> modalFunctions;
    public final PropModifierKeyConfigurationsMapStruct propModifiers = new PropModifierKeyConfigurationsMapStruct("prop_modifiers");
    public final HotKeyConfigRegistry hotKeyConfigRegistry;

    public ModalConfigurationsGroupStruct(String id, HotKeyConfigRegistry registry) {
        super(id);
        this.hotKeyConfigRegistry = registry;
        this.addChild("modal_functions", this.modalFunctions = new ModalFuncKeyConfigListStruct<>("modal_functions", this.hotKeyConfigRegistry));
        this.addChild("prop_modifiers", this.propModifiers);
    }
}
