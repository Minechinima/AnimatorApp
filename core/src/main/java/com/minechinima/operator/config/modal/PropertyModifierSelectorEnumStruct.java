package com.minechinima.operator.config.modal;

import com.minechinima.model.values.AbstractEnumValueHierarchyReg;
import com.minechinima.operator.OperatorProperties;
import org.jetbrains.annotations.NotNull;

public class PropertyModifierSelectorEnumStruct<P extends OperatorProperties>
        extends AbstractEnumValueHierarchyReg<PropertyModifierCommandConfig<P>> {
    public PropertyModifierSelectorEnumStruct(String id, PropertyModifiersList<P> category, @NotNull PropertyModifierCommandConfig<P> defaultValue) {
        super(id, category, defaultValue);
    }
}
