package com.minechinima.operator.config;

import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorTypeRegistryGroupStruct;
import com.minechinima.operator.config.modal.ModalFunctionConfigsMapStruct;
import com.minechinima.operator.config.modal.PropertyModifierConfigsMapStruct;
import com.minechinima.operator.config.operator.OperatorKeyConfigItemGroupStruct;
import com.minechinima.operator.config.operator.OperatorKeyConfigListStruct;
import com.minechinima.operator.config.operator.OperatorConfigCategoriesMapStruct;
import com.minechinima.ui.elements.UISpace;

import java.util.*;

/**
 * This class has all the configuration data for the user functionality
 *
 */
public class UserFunctionalityConfigGroupStruct<C extends IContext> extends ValueGroup {
    //TODO we need a cache of config categories for their UI identifiers for shallow and quick search
    // when the operator manager retrieves categories for a specific ui.
    // impl idea: listener when children get addeed -> ui identifiers are final, add them here into a map.
    public final OperatorConfigCategoriesMapStruct<C> operatorKeyConfigs;
    public final ModalFunctionConfigsMapStruct<C> modalFunctions;
    public final PropertyModifierConfigsMapStruct propModifiers;

    public final HotKeyConfigRegistry hotKeyConfigRegistry;
    public final OperatorTypeRegistryGroupStruct<C> operatorTypeRegistry;

    public UserFunctionalityConfigGroupStruct(String id, HotKeyConfigRegistry hotKeyConfigRegistry, OperatorTypeRegistryGroupStruct<C> operatorTypeRegistry) {
        super(id);
        this.hotKeyConfigRegistry = hotKeyConfigRegistry;
        this.operatorTypeRegistry = operatorTypeRegistry;
        this.addChild("configs", this.operatorKeyConfigs = new OperatorConfigCategoriesMapStruct<>("key_configurations"));
        this.addChild("modal_functions", this.modalFunctions = new ModalFunctionConfigsMapStruct<>("modal_functions", this.hotKeyConfigRegistry));
        this.addChild("prop_modifiers", this.propModifiers = new PropertyModifierConfigsMapStruct("prop_modifiers"));
    }

    //TODO for testing
    public OperatorKeyConfigItemGroupStruct<C> newConfig() {
        OperatorKeyConfigItemGroupStruct<C> config = new OperatorKeyConfigItemGroupStruct<>(this);
        config.initChildren();
        OperatorKeyConfigListStruct<C> list = new OperatorKeyConfigListStruct<>(String.valueOf((int) (Math.random() * 1000)), "test");
        list.add(config);
        this.operatorKeyConfigs.add(list);
        return config;
    }

    public List<OperatorKeyConfigItemGroupStruct<C>> getConfigsForUISpace(Class<UISpace> uispace) {
        return null;
    }

    /**
     * TODO just for testing
     * @return configs that respond to the event
     */
    public Set<OperatorKeyConfigItemGroupStruct<C>> getConfigs(KeyMapEvent event) {
        Set<OperatorKeyConfigItemGroupStruct<C>> collected = new HashSet<>();
        for (Map.Entry<String, OperatorKeyConfigListStruct<C>> entries : this.operatorKeyConfigs) {
            for (OperatorKeyConfigItemGroupStruct<C> config : entries.getValue()) {
                HotKeyConfigGroupStruct hotkey;
                if ((hotkey = config.hotKeyConfigSelector.get()) == null) continue;
                if (hotkey.onEvent(event)) collected.add(config);
            }
        }

        return collected;
    }
}
