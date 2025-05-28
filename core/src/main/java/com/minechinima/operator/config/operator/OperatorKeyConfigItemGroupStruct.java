package com.minechinima.operator.config.operator;

import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.KeyConfigurationsGroupStruct;
import com.minechinima.operator.ot.OperatorType;

/**
 * Configuration for a single operator type hotkey.
 * @param <C>
 */
public class OperatorKeyConfigItemGroupStruct<C extends IContext> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final ValuePointer<OperatorType> operatorType;
    public final OperatorConfigPropsTreeStruct props;

    public OperatorKeyConfigItemGroupStruct(KeyConfigurationsGroupStruct<C> opConfig) {
        super("");
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", opConfig.hotKeyConfigRegistry));
        this.addChild("operator_type",
                this.operatorType = new ValuePointer<>("operator_type", OperatorType.class, opConfig.operatorTypeRegistry));
        this.addChild("properties",
                this.props = new OperatorConfigPropsTreeStruct("properties", this.operatorType));
    }
}
