package com.minechinima.operator.config.modal;

import com.minechinima.model.values.AbstractEnumValueHierarchyReg;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ModalFunctionGroupStruct;
import org.jetbrains.annotations.NotNull;

public class ModalFunctionSelectorEnumStruct<C extends IContext> extends AbstractEnumValueHierarchyReg<ModalFunctionGroupStruct<C>> {
    public ModalFunctionSelectorEnumStruct(String id, ModalFunctionConfigsMapStruct<C> category, @NotNull ModalFunctionGroupStruct<C> defaultValue) {
        super(id, category, defaultValue);
    }
}
