package com.minechinima.depsgraph;

import com.minechinima.depsgraph.builder.ComponentBuilder;
import com.minechinima.depsgraph.tagging.NodeTag;
import com.minechinima.depsgraph.tagging.NodeTaggingCreator;
import com.minechinima.kernel.ConstraintEvalRegistry;
import com.minechinima.kernel.Constraints;
import com.minechinima.model.app.object.*;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.ChangeEvent;
import com.minechinima.model.events.types.changes.ListAddEvent;
import com.minechinima.model.events.types.changes.ListIndexChangeEvent;
import com.minechinima.model.events.types.changes.ListRemoveEvent;
import com.minechinima.model.logging.operations.ListChangeIndex;
import org.jetbrains.annotations.Nullable;

public class ConstraintsComponentBuilder<DATA extends IConstraintsComponent, EVAL extends IConstraintsComponent & IEvaluatedTransformation>
        extends ComponentBuilder<DATA, EVAL, ITransformationComponent> {
    private final ConstraintEvalRegistry constraintEvalRegistry;

    public ConstraintsComponentBuilder(ConstraintEvalRegistry constraintEvalRegistry) {
        super(ComponentTypes.CONSTRAINTS);
        this.constraintEvalRegistry = constraintEvalRegistry;
        this.addOperationNodeBuilder((obj, helper) ->
                Constraints.buildConstraints(obj, constraintEvalRegistry, helper));
        this.addComponentRelationBuilder(Constraints::buildConstraintComponentRelation);
        this.addComponentTaggingConfig(component -> NodeTaggingCreator.forData(component.constraints(), NodeTag.NEEDS_REEVALUATION));
        this.addComponentTaggingConfig(component -> NodeTaggingCreator.forEventConstraints(component.constraints(),
                ChangeEvent.class, SubscribeEvent.Type.ON_NODE, NodeTag.REBUILD_TREE));
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
