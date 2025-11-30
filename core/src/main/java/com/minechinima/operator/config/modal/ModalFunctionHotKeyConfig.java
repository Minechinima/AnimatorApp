package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.ReleaseConfirmHotKey;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
