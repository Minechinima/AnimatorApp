package com.minechinima.depsgraph;

import com.minechinima.depsgraph.builder.ComponentBuilder;
import com.minechinima.kernel.Transform;
import com.minechinima.model.api.IData;
import com.minechinima.model.app.object.IEvaluatedTransformation;
import com.minechinima.model.app.object.ITransformationComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TransformComponentBuilder<DATA extends ITransformationComponent, EVAL extends IEvaluatedTransformation>
        extends ComponentBuilder<DATA, EVAL, ITransformationComponent> {
    public TransformComponentBuilder() {
        super(ComponentTypes.TRANSFORM);
        this.addOperationNodeBuilder(Transform::buildTransformComponent);
        this.addComponentRelationBuilder(Transform::buildTransformRelations);
    }

    @Nullable
    @Override
    public DataScope getDataScope(DATA data) {
        return new DataScope(Set.of(data.scale(), data.rotation(), data.translation(), data.parent()), Set.of());
    }

    @Override
    public void addEntryExitOperation(ComponentNode<DATA, EVAL, ITransformationComponent> component) {
        component.setEntry(component.addOperation(null, OperationCodes.TRANSFORM_ENTRY, ""));
        component.setExit(component.addOperation(null, OperationCodes.TRANSFORM_EXIT, ""));

    }
}
