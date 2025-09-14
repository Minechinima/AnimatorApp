package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Contains all property modifier configurations
 */
public class PropertyModifierConfigsMapStruct extends BaseValueMap<PropertyModifiersListStruct<?>> {
    public PropertyModifierConfigsMapStruct(String id) {
        super(id);
    }

    @Override
    protected @NotNull PropertyModifiersListStruct instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        return null;
    }

    @Nullable
    public <P extends OperatorPropertiesGroupStruct> PropertyModifiersListStruct<P> getModalKeymapForProps(P props) {
        for (PropertyModifiersListStruct<?> config : this.getChildren()) {
            if (config.clazz.isAssignableFrom(props.getClass())) {
                return (PropertyModifiersListStruct<P>) config;
            }
        }

        return null;
    }
}
