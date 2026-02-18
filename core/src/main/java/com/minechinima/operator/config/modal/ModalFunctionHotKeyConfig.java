package com.minechinima.operator.config.modal;

import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.ReleaseConfirmHotKey;

public class ModalFunctionHotKeyConfig<C extends IContext> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final ModalFunctionSelectorEnumStruct<C> modalFunction;
    public final ReleaseConfirmHotKey<ModalFunctionSelectorEnumStruct<C>> modalConfirmConfig;

    public ModalFunctionHotKeyConfig(String id, ModalFunctionConfigsMapStruct<C> category) {
        super(id);
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", category.hotKeyConfigRegistry));
        this.addChild("modifier_config", this.modalFunction = new ModalFunctionSelectorEnumStruct<>("modifier_config", category,null));
        this.addChild("modal_confirm_config", this.modalConfirmConfig = new ReleaseConfirmHotKey<>("modal_confirm_config", this.hotKeyConfigSelector, this.modalFunction));
    }
}
