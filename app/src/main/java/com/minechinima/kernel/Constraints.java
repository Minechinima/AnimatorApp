package com.minechinima.kernel;

import com.minechinima.depsgraph.*;
import com.minechinima.model.app.constraints.ChildOfConstraint;
import com.minechinima.model.app.constraints.ConstraintContainer;
import com.minechinima.model.app.object.IConstraintsComponent;
import com.minechinima.model.app.object.IEvaluatedTransformation;

public class Constraints {
    public static <T extends IConstraintsComponent & IEvaluatedTransformation> void
    childOfConstraint(DepsGraph graph, int constraintIndex, T evaluatedData) {
        ConstraintContainer c = evaluatedData.constraints().getChildren().get(constraintIndex);
        if (c == null || !(c.get() instanceof ChildOfConstraint constraint)) return;

        if (constraint.target.get() == null) return;

        // TODO OMG the constraint passed to this is the not evaluated database struct.
    }


    /**
     * Function to calculate the transformation of one constraint.
     * Pass the constraint index, so the eval function can retrieve the evaluated constraint copy.
     */
    @FunctionalInterface
    public interface ConstraintEval<T extends IConstraintsComponent & IEvaluatedTransformation> {
        void eval(DepsGraph graph, int constraintIndex, T evaluatedData);
    }
}
