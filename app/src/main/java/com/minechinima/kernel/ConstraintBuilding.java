package com.minechinima.kernel;

import com.minechinima.depsgraph.*;
import com.minechinima.depsgraph.builder.ComponentBuilder;
import com.minechinima.depsgraph.tagging.NodeTag;
import com.minechinima.depsgraph.tagging.NodeTaggingCreator;
import com.minechinima.model.app.constraints.ConstraintContainer;
import com.minechinima.model.app.constraints.ConstraintStruct;
import com.minechinima.model.app.object.IConstraintsComponent;
import com.minechinima.model.app.object.IEvaluatedTransformation;
import com.minechinima.model.app.object.ITransformationComponent;

import java.util.List;

import static com.minechinima.kernel.Constraints.childOfConstraint;

public class ConstraintBuilding {
    /**
     * Builds the operations for the constraints
     * @param data
     * @param evalRegistry
     * @param helper
     */
    public static <DATA extends IConstraintsComponent, EVAL extends IConstraintsComponent & IEvaluatedTransformation>
    void buildConstraints(DATA data, ConstraintEvalRegistry evalRegistry, ComponentBuilder<DATA, EVAL, ITransformationComponent>.OperationNodesBuilderHelper helper) {
        List<ConstraintContainer> constraints = data.constraints().getChildren();
        for (int i = 0; i < constraints.size(); i++) {
            final int constraintIndex = i;
            ConstraintStruct constraint = constraints.get(i).get();
            if (constraint == null) continue;
            ConstraintBuilding.ConstraintOperationNodeBuilder<DATA, EVAL> func = evalRegistry.getEvalForConstraint(constraint.getClass());
            if (func == null) continue;

            func.buildConstraintOperationNode(data, constraintIndex, helper);
        }

        helper.chainToExit();
    }

    public static void buildConstraintComponentRelation(DepsGraph graph,
                                                        ComponentNode<? extends ITransformationComponent, ? extends IEvaluatedTransformation, ITransformationComponent> componentNode) {
        ComponentNode transformNode = componentNode.getOwner().getComponent(ComponentTypes.TRANSFORM);
        if (transformNode == null) {
            return;
        }
        /* transformation needs to be calculated before constraints */
        transformNode.addFromRelationTo(componentNode, "Transform -> Constraints");
    }

    /**
     * Build relation to the constraint target object.
     * Simple default target relation builder
     */
    public static void constraintTargetRelation(DepsGraph graph,
                                                ConstraintStruct constraint,
                                                OperationNode<? extends ITransformationComponent> constraintOperation) {
        if (constraint.target.get() != null) {
            DataNode node = graph.getDataNodeForData(constraint.target.get());
            if (node == null) {
                return;
            }
            ComponentNode transformTarget = node.getComponent(ComponentTypes.TRANSFORM);
            if (transformTarget == null) {
                return;
            }
            transformTarget.addFromRelationTo(constraintOperation, "Constraint relation");
        }

    }

    public static <EVAL extends IConstraintsComponent & IEvaluatedTransformation>
    void buildChildOfConstraint(IConstraintsComponent data,
                                final int constraintIndex,
                                ComponentBuilder<IConstraintsComponent, EVAL, ITransformationComponent>.OperationNodesBuilderHelper helper) {
        ConstraintStruct constraint = data.constraints().get(constraintIndex).get();
        helper.addOperation((g, d) -> childOfConstraint(g, constraintIndex, d),
                OperationCodes.CONSTRAINTS_EVAL,
                String.valueOf(constraintIndex),
                new DataScope(constraint),
                (g, n) -> ConstraintBuilding.constraintTargetRelation(g, constraint, n),
                NodeTaggingCreator.forData(constraint.target, NodeTag.REBUILD_TREE)).addToChain();
    }

    @FunctionalInterface
    public interface ConstraintOperationNodeBuilder<DATA extends IConstraintsComponent, EVAL extends IConstraintsComponent & IEvaluatedTransformation> {
            void buildConstraintOperationNode(DATA data, final int constraintIndex, ComponentBuilder<DATA, EVAL, ITransformationComponent>.OperationNodesBuilderHelper helper);
    }
}
