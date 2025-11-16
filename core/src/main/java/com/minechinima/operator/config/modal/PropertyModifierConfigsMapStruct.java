package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Each item in this map is a configuration for all the modifiers of one property type.
 */
public class PropertyModifierConfigsMapStruct extends BaseValueMap<PropertyModifiersListStruct<?>> {
    public PropertyModifierConfigsMapStruct(String id) {
        super(id);
    }

    @Override
    protected @NotNull PropertyModifiersListStruct instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        return null;
    }

    /**
     *
     * @param props
     * @return all property modifier configurations that are assignable to the property class or subclasses.
     * @param <P>
     */
    public <P extends OperatorPropertiesGroupStruct> List<PropertyModifierHotKeyConfig<P>> getConfigs(P props) {
        List<PropertyModifierHotKeyConfig<P>> list = new ArrayList<>();
        for (PropertyModifiersListStruct<?> config : this.getChildren()) {
            if (config.clazz.isAssignableFrom(props.getClass())) {
                //TODO go through the modifiers and replace configs from subclasses where the modifier class is equal
                // this allows for configurations for subclasses of props to overwrite modifier behavior of the superclass
                list.addAll((Collection<? extends PropertyModifierHotKeyConfig<P>>) config.getChildren());
            }
        }

        return list;
    }
}
