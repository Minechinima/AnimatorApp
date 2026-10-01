package com.minechinima.depsgraph;

import com.minechinima.depsgraph.builder.ComponentBuilder;
import com.minechinima.depsgraph.tagging.NodeTag;
import com.minechinima.depsgraph.tagging.NodeTagging;
import com.minechinima.kernel.ConstraintBuilding;
import com.minechinima.kernel.ConstraintEvalRegistry;
import com.minechinima.model.app.object.*;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.ChangeEvent;
import org.jetbrains.annotations.Nullable;

public class ConstraintsComponentBuilder<DATA extends IConstraintsComponent, EVAL extends IConstraintsComponent & IEvaluatedTransformation>
        extends ComponentBuilder<DATA, EVAL, ITransformationComponent> {
    private final ConstraintEvalRegistry constraintEvalRegistry;

    public ConstraintsComponentBuilder(ConstraintEvalRegistry constraintEvalRegistry) {
        super(ComponentTypes.CONSTRAINTS);
        this.constraintEvalRegistry = constraintEvalRegistry;
        this.addOperationNodeBuilder((obj, helper) ->
                ConstraintBuilding.buildConstraints(obj, constraintEvalRegistry, helper));
        this.addComponentRelationBuilder(ConstraintBuilding::buildConstraintComponentRelation);
        /* for any parameter change in constraints update the entire component */
        this.addComponentTaggingConfig(component -> NodeTagging.forData(component.constraints(), NodeTag.NEEDS_REEVALUATION));
        /* changes in the constraint list require rebuilding relations */
        this.addComponentTaggingConfig(component -> NodeTagging.forEventConstraints(component.constraints(),
                ChangeEvent.class, SubscribeEvent.Type.ON_NODE, NodeTag.REBUILD_TREE));
    }

    @Nullable
    @Override
    public DataScope getDataScope(DATA data) {
        return new DataScope(data.constraints());
    }

    @Override
    public void addEntryExitOperation(ComponentNode<DATA, EVAL, ITransformationComponent> component) {
        component.setEntry(component.addOperation(null, OperationCodes.CONSTRAINTS_ENTRY));
        component.setExit(component.addOperation(null, OperationCodes.CONSTRAINTS_EXIT));
    }
}
