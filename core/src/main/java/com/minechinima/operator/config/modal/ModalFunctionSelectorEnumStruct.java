package com.minechinima.operator.config.modal;

import com.minechinima.model.values.AbstractEnumValueHierarchyReg;
import com.minechinima.operator.IContext;
import org.jetbrains.annotations.NotNull;

public class ModalFunctionSelectorEnumStruct<C extends IContext> extends AbstractEnumValueHierarchyReg<ModalFunctionConfig<C>> {
    public ModalFunctionSelectorEnumStruct(String id, ModalFunctionConfigsMapStruct<C> category, @NotNull ModalFunctionConfig<C> defaultValue) {
        super(id, category, defaultValue);
    }
}
