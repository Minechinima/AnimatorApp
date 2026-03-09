package com.minechinima.depsgraph;

import com.minechinima.depsgraph.builder.ComponentBuilder;
import com.minechinima.kernel.ConstraintEvalRegistry;
import com.minechinima.kernel.Constraints;
import com.minechinima.kernel.Object;
import com.minechinima.model.api.IData;
import com.minechinima.model.app.object.*;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ConstraintsComponentBuilder<DATA extends IConstraintsComponent, EVAL extends IConstraintsComponent & IEvaluatedTransformation>
        extends ComponentBuilder<DATA, EVAL, ITransformationComponent> {
    private final ConstraintEvalRegistry constraintEvalRegistry;

    public ConstraintsComponentBuilder(ConstraintEvalRegistry constraintEvalRegistry) {
        super(ComponentTypes.CONSTRAINTS);
        this.constraintEvalRegistry = constraintEvalRegistry;
        this.addOperationNodeBuilder((obj, helper) ->
                Constraints.buildConstraints(obj, constraintEvalRegistry, helper));
        this.addComponentRelationBuilder(Constraints::buildConstraintComponentRelation);
    }

    @Nullable
    @Override
    public DataScope getDataScope(DATA data) {
        return null;
    }

    @Override
    public void addEntryExitOperation(ComponentNode<DATA, EVAL, ITransformationComponent> component) {

    }
}
