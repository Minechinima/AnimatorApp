package com.minechinima.operator.config.modal;

import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.config.ReleaseConfirmHotKey;

/**
 * Configuration for a {@link PropertyModifierCommandConfig}
 * @param <P>
 */
public class PropertyModifierHotKeyConfig<P extends OperatorProperties> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final PropertyModifierSelectorEnumStruct<P> modifier;
    public final ReleaseConfirmHotKey<PropertyModifierSelectorEnumStruct<P>> modalConfirmConfig;

    public PropertyModifierHotKeyConfig(PropertyModifiersList<P> category) {
        super("");
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", category.hotKeyConfigRegistry));
        this.addChild("modifier_config", this.modifier = new PropertyModifierSelectorEnumStruct<>("modifier_config", category,null));
        this.addChild("modal_confirm_config", this.modalConfirmConfig = new ReleaseConfirmHotKey<>("modal_confirm_config", this.hotKeyConfigSelector, this.modifier));
    }
}
