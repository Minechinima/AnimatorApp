package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.NullType;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.ReleaseConfirmHotKey;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Configuration for a single operator type hotkey.
 * @param <C>
 */
public class OperatorConfigGroupStruct<C extends IContext> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final ValuePointer<OperatorType> operatorType;
    public final OperatorConfigPropsTreeStruct props;
    public final ReleaseConfirmHotKey<ValuePointer<OperatorType>> modalConfirmConfig;

    private final IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry;

    public OperatorConfigGroupStruct(UserFunctionalityConfigGroupStruct<C> opConfig) {
        super("");
        this.hotKeyConfigRegistry = opConfig.hotKeyConfigRegistry;

        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", this.hotKeyConfigRegistry));
        this.addChild("operator_type",
                this.operatorType = new ValuePointer<>("operator_type", OperatorType.class, opConfig.operatorTypeRegistry));
        this.addChild("properties",
                this.props = new OperatorConfigPropsTreeStruct("properties", this.operatorType));
        this.addChild("modal_confirm_config", this.modalConfirmConfig = new ReleaseConfirmHotKey<>("modal_confirm_config", this.hotKeyConfigSelector, this.operatorType));
    }
}
