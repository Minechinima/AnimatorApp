package com.minechinima.model.app.constraints;

import com.minechinima.model.values.AbstractEnumValueHierarchy;
import com.minechinima.model.values.AbstractEnumValueHierarchyReg;
import com.minechinima.utils.IGenericRegistry;

/**
 * Constraint container that holds the concrete constraint data.
 * Is an enum so every constraint can be changed later on.
 *
 */
//TODO maybe unecessary. Many places use enum hierarchy for wrapping a datastruct just so the UI
// can fetch the registry.
public class ConstraintContainer extends AbstractEnumValueHierarchyReg<ConstraintStruct> {
    public ConstraintContainer(String id, IGenericRegistry<ConstraintStruct> registry) {
        super(id, registry);
    }
}
