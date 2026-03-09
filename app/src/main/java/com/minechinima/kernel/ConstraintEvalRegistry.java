package com.minechinima.kernel;

import com.minechinima.depsgraph.OperationNode;
import com.minechinima.depsgraph.builder.RelationBuilder;
import com.minechinima.model.api.IData;
import com.minechinima.model.app.constraints.ConstraintStruct;
import com.minechinima.model.app.object.BaseObjectRuntime;
import com.minechinima.model.app.object.ITransformationComponent;
import org.jetbrains.annotations.Nullable;

/**
 * Registry for the evaluation logic of a constraint data struct.
 */
public class ConstraintEvalRegistry {
    // TODO maybe use enums instead of classes. Further decouples and allows more reusability separate from data definitions
    @Nullable
    public <C extends ConstraintStruct> Constraints.ConstraintEval<?> getEvalForConstraint(Class<C> constraintType) {
        return null;
    }

    @Nullable
    public <C extends ConstraintStruct> Constraints.ConstraintRelationBuilder<C> getRelationBuilderForConstraint(Class<C> constraintType) {
        return null;
    }
}
