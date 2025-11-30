package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.config.ReleaseConfirmHotKey;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Configuration for a {@link PropertyModifierConfig}
 * @param <P>
 */
public class PropertyModifierHotKeyConfig<P extends OperatorPropertiesGroupStruct> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final PropertyModifierSelectorEnumStruct<P> modifier;
    public final ReleaseConfirmHotKey<PropertyModifierSelectorEnumStruct<P>> modalConfirmConfig;

    public PropertyModifierHotKeyConfig(PropertyModifiersListStruct<P> category) {
        super("");
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", category.hotKeyConfigRegistry));
        this.addChild("modifier_config", this.modifier = new PropertyModifierSelectorEnumStruct<>("modifier_config", category,null));
        this.addChild("modal_confirm_config", this.modalConfirmConfig = new ReleaseConfirmHotKey<>("modal_confirm_config", this.hotKeyConfigSelector, this.modifier));
    }
}
