package com.minechinima.operator.config.operator;

import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.base.ValueGroup;

/**
 * Optional configuration when the operator is a modal operator how it should be confirmed.
 */
public class OperatorModalConfirmConfig extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final BooleanValue confirmOnRelease;

    public OperatorModalConfirmConfig(HotKeyConfigRegistry hotKeyConfigRegistry) {
        this(false, hotKeyConfigRegistry);
    }

    public OperatorModalConfirmConfig(boolean confirmOnEvent, HotKeyConfigRegistry hotKeyConfigRegistry) {
        super("");
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", hotKeyConfigRegistry));
        this.addChild("hotkey_config",
                this.confirmOnRelease = new BooleanValue("confirm_on_event", confirmOnEvent));
    }
}
