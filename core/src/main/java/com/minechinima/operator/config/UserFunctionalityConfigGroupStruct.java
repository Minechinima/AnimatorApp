package com.minechinima.operator.config;

import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorTypeRegistryGroupStruct;
import com.minechinima.operator.config.modal.ModalConfigurationsGroupStruct;
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
    public final OperatorConfigCategoriesMapStruct<C> operatorKeyConfigs;
    public final ModalConfigurationsGroupStruct<C> modalKeyConfigs;

    public final HotKeyConfigRegistry hotKeyConfigRegistry;
    public final OperatorTypeRegistryGroupStruct<C> operatorTypeRegistry;

    public UserFunctionalityConfigGroupStruct(String id, HotKeyConfigRegistry hotKeyConfigRegistry, OperatorTypeRegistryGroupStruct<C> operatorTypeRegistry) {
        super(id);
        this.hotKeyConfigRegistry = hotKeyConfigRegistry;
        this.operatorTypeRegistry = operatorTypeRegistry;
        this.addChild("configs", this.operatorKeyConfigs = new OperatorConfigCategoriesMapStruct<>("key_configurations"));
        this.addChild("modal_keymap", this.modalKeyConfigs = new ModalConfigurationsGroupStruct<>("modal_keymap", this.hotKeyConfigRegistry));
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
