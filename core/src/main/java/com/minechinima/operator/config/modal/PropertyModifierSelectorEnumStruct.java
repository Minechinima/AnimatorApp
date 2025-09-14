package com.minechinima.operator.config.modal;

import com.minechinima.model.values.AbstractEnumValueHierarchyReg;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.PropertyModifierGroupStruct;
import org.jetbrains.annotations.NotNull;

public class PropertyModifierSelectorEnumStruct<P extends OperatorPropertiesGroupStruct>
        extends AbstractEnumValueHierarchyReg<PropertyModifierGroupStruct<P>> {
    public PropertyModifierSelectorEnumStruct(String id, PropertyModifiersListStruct<P> category, @NotNull PropertyModifierGroupStruct<P> defaultValue) {
        super(id, category, defaultValue);
    }
}
