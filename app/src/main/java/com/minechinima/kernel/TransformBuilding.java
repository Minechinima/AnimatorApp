package com.minechinima.kernel;

import com.minechinima.depsgraph.*;
import com.minechinima.depsgraph.builder.OperationNodesBuilderHelper;
import com.minechinima.model.app.object.IEvaluatedTransformation;
import com.minechinima.model.app.object.ITransformationComponent;

public class TransformBuilding {
    public static <DATA extends ITransformationComponent>
    void buildTransformRelations(DepsGraph graph, ComponentNode<DATA, ?, ITransformationComponent> transform) {
        DATA obj = transform.getOwner().getOriginalData();
        if (obj.parent().get() != null) {
            DataNode parentNode = graph.getDataNodeForData(obj.parent().get(), ITransformationComponent.class);
            ComponentNode parentTransform;
            if ((parentTransform = parentNode.getComponent(ComponentTypes.TRANSFORM)) != null) {
                parentTransform.addFromRelationTo(transform, "parent");
            }
        }
    }

    public static <DATA extends ITransformationComponent, EVAL extends IEvaluatedTransformation>
    void buildTransformComponent(DATA obj, OperationNodesBuilderHelper<DATA, EVAL, ITransformationComponent> helper) {
        helper.addOperation(Transform::calculateLocalTransformation, OperationCodes.TRANSFORM_LOCAL)
                .addToChain();
        if (obj.parent().get() != null) {
            helper.addOperation(Transform::calculateParentTransformation, OperationCodes.TRANSFORM_PARENT)
                    .addToChain();
        }

        helper.chainToExit();
    }
}
