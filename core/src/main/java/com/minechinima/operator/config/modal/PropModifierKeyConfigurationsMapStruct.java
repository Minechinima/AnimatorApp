package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Contains all available keymaps for property modifiers
 */
public class PropModifierKeyConfigurationsMapStruct extends BaseValueMap<PropModifierKeyConfigListStruct<?>> {
    public PropModifierKeyConfigurationsMapStruct(String id) {
        super(id);
    }

    @Override
    protected @NotNull PropModifierKeyConfigListStruct instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        return null;
    }

    @Nullable
    public <P extends OperatorPropertiesGroupStruct> PropModifierKeyConfigListStruct<P> getModalKeymapForProps(P props) {
        for (PropModifierKeyConfigListStruct<?> config : this.getChildren()) {
            if (config.clazz.isAssignableFrom(props.getClass())) {
                return (PropModifierKeyConfigListStruct<P>) config;
            }
        }

        return null;
    }
}
