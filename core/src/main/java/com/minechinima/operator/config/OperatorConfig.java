package com.minechinima.operator.config;

import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnum;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorTypeRegistry;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UIEnum;

/**
 * Configuration for a single operator type.
 * @param <C>
 */
public class OperatorConfig<C extends IContext> extends ValueGroup {
    public final HotKeyConfigSelectorEnum hotKeyConfigSelector;
    public final ValuePointer<OperatorType> operatorType;
    public final OperatorConfigProps props;
    //TODO space/region type

    public OperatorConfig(OperatorConfigurations<C> opConfig) {
        super("");
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnum("hotkey_config", opConfig.hotKeyConfigRegistry));
        this.addChild("operator_type",
                this.operatorType = new ValuePointer<>("operator_type", OperatorType.class, opConfig.operatorTypeRegistry));
        this.addChild("properties",
                this.props = new OperatorConfigProps("properties", this.operatorType));
        this.getEventBus().subscribe(this);
    }
}
