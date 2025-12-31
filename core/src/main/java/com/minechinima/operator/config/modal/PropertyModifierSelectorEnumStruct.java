package com.minechinima.operator.config.modal;

import com.minechinima.model.values.AbstractEnumValueHierarchyReg;
import com.minechinima.operator.OperatorProperties;
import org.jetbrains.annotations.NotNull;

public class PropertyModifierSelectorEnumStruct<P extends OperatorProperties>
        extends AbstractEnumValueHierarchyReg<PropertyModifierConfig<P>> {
    public PropertyModifierSelectorEnumStruct(String id, PropertyModifiersListStruct<P> category, @NotNull PropertyModifierConfig<P> defaultValue) {
        super(id, category, defaultValue);
    }
}
