package com.minechinima.depsgraph;

import com.minechinima.depsgraph.builder.ComponentBuilder;
import com.minechinima.depsgraph.tagging.NodeTag;
import com.minechinima.depsgraph.tagging.NodeTagging;
import com.minechinima.kernel.TransformBuilding;
import com.minechinima.model.app.object.IEvaluatedTransformation;
import com.minechinima.model.app.object.ITransformationComponent;
import org.jetbrains.annotations.Nullable;

public class TransformComponentBuilder<DATA extends ITransformationComponent, EVAL extends IEvaluatedTransformation>
        extends ComponentBuilder<DATA, EVAL, ITransformationComponent> {
    public TransformComponentBuilder() {
        super(ComponentTypes.TRANSFORM);
        this.addOperationNodeBuilder(TransformBuilding::buildTransformComponent);
        this.addComponentRelationBuilder(TransformBuilding::buildTransformRelations);
        this.addComponentTaggingConfig(transform -> NodeTagging.forDataScope(this.getDataScope(transform), NodeTag.NEEDS_REEVALUATION));
        this.addComponentTaggingConfig(transform -> NodeTagging.forData(transform.parent(), NodeTag.REBUILD_TREE));
    }

    @Nullable
    @Override
    public DataScope getDataScope(DATA data) {
        return new DataScope(data.scale(), data.rotation(), data.translation(), data.parent());
    }

    @Override
    public void addEntryExitOperation(ComponentNode<DATA, EVAL, ITransformationComponent> component) {
        component.setEntry(component.addOperation(null, OperationCodes.TRANSFORM_ENTRY, ""));
        component.setExit(component.addOperation(null, OperationCodes.TRANSFORM_EXIT, ""));

    }
}
