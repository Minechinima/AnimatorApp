package com.minechinima.operator.config;

import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.modal.ModalFuncKeyConfigListStruct;
import com.minechinima.operator.config.modal.PropModifierKeyConfigurationsMapStruct;

public class ModalKeyConfigurationsGroupStruct<C extends IContext> extends ValueGroup {
    public final ModalFuncKeyConfigListStruct<C> modalFunctions;
    public final PropModifierKeyConfigurationsMapStruct propModifiers = new PropModifierKeyConfigurationsMapStruct("prop_modifiers");
    public final HotKeyConfigRegistry hotKeyConfigRegistry;

    public ModalKeyConfigurationsGroupStruct(String id, HotKeyConfigRegistry registry) {
        super(id);
        this.hotKeyConfigRegistry = registry;
        this.addChild("modal_functions", this.modalFunctions = new ModalFuncKeyConfigListStruct<>("modal_functions", this.hotKeyConfigRegistry));
        this.addChild("prop_modifiers", this.propModifiers);
    }
}
