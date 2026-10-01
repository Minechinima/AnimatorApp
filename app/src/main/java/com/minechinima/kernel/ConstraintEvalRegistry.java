package com.minechinima.kernel;

import com.minechinima.model.app.constraints.ConstraintStruct;
import org.jetbrains.annotations.Nullable;

/**
 * Registry for the evaluation logic of a constraint data struct.
 */
public class ConstraintEvalRegistry {
    // TODO maybe use enums instead of classes. Further decouples and allows more reusability separate from data definitions
    @Nullable
    public ConstraintBuilding.ConstraintOperationNodeBuilder getEvalForConstraint(Class<? extends ConstraintStruct> constraintType) {
        return null;
    }

}
