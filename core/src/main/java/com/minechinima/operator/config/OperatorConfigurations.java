package com.minechinima.operator.config;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfig;
import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseValueList;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorTypeRegistry;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.ui.elements.UISpace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This class has all the configuration data for the user functionality
 */
public class OperatorConfigurations<C extends IContext> extends ValueGroup {
    public final BaseValueList<OperatorConfig<C>> configs = new BaseValueList<>("configs") {
        @Override
        protected @NotNull OperatorConfig<C> instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
            return null;
        }
    };
    public final HotKeyConfigRegistry hotKeyConfigRegistry;
    public final OperatorTypeRegistry<C> operatorTypeRegistry;

    public OperatorConfigurations(String id, HotKeyConfigRegistry hotKeyConfigRegistry, OperatorTypeRegistry<C> operatorTypeRegistry) {
        super(id);
        this.hotKeyConfigRegistry = hotKeyConfigRegistry;
        this.operatorTypeRegistry = operatorTypeRegistry;
        this.addChild("configs", this.configs);
    }

    public OperatorConfig<C> newConfig() {
        OperatorConfig<C> config = new OperatorConfig<>(this);
        config.initChildren();
        this.configs.add(config);
        return config;
    }

    public List<OperatorConfig<C>> getConfigsForUISpace(Class<UISpace> uispace) {
        return null;
    }

    /**
     * TODO just for testing
     * @return configs that respond to the event
     */
    public Set<OperatorConfig<C>> getConfigs(KeyMapEvent event) {
        Set<OperatorConfig<C>> collected = new HashSet<>();
        for (OperatorConfig<C> config : this.configs) {
            HotKeyConfig hotkey;
            if ((hotkey = config.hotKeyConfigSelector.get()) == null) continue;
            if (hotkey.onEvent(event)) collected.add(config);
        }
        return collected;
    }
}
