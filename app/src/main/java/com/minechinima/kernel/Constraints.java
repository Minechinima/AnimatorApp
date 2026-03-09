package com.minechinima.kernel;

import com.minechinima.depsgraph.*;
import com.minechinima.depsgraph.builder.ComponentBuilder;
import com.minechinima.depsgraph.builder.RelationBuilder;
import com.minechinima.model.app.constraints.ChildOfConstraint;
import com.minechinima.model.app.constraints.ConstraintContainer;
import com.minechinima.model.app.constraints.ConstraintStruct;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.app.object.IConstraintsComponent;
import com.minechinima.model.app.object.IEvaluatedTransformation;
import com.minechinima.model.app.object.ITransformationComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Constraints {
    /**
     * Builds the operations for the constraints
     * @param data
     * @param evalRegistry
     * @param helper
     */
    public static <DATA extends IConstraintsComponent, EVAL extends IConstraintsComponent & IEvaluatedTransformation>
    void buildConstraints(DATA data, ConstraintEvalRegistry evalRegistry, ComponentBuilder<DATA, EVAL, ITransformationComponent>.OperationNodesBuilderHelper helper) {
        // TODO new update graph topology use case: List updates, not single operation node data.
        //  need a more generic way to register when to update graph topology
        // maybe a generic way to register listeners for any node besides the relation data thing.

        List<ConstraintContainer> constraints = data.constraints().getChildren();
        for (int i = 0; i < constraints.size(); i++) {
            final int constraintIndex = i;
            ConstraintContainer container = constraints.get(i);
            ConstraintStruct constraint = container.get();
            if (constraint == null) continue;

            ConstraintEval func = evalRegistry.getEvalForConstraint(constraint.getClass());
            if (func == null) continue;

            OperationNode.EvalFunction<EVAL> evalFunc = (g, evalData) -> func.eval(g, constraintIndex, evalData);

            RelationBuilder<OperationNode<EVAL>> relationBuilder = relationBuilderGenericHelper(evalRegistry, constraint, helper);

            helper.addOperation(evalFunc,
                    OperationCodes.CONSTRAINTS,
                    String.valueOf(i),
                    new DataScope(constraint),
                    relationBuilder).addToChain();
        }

        helper.chainToExit();
    }


    @Nullable
    private static <C extends ConstraintStruct, EVAL extends ITransformationComponent> RelationBuilder<OperationNode<EVAL>>
    relationBuilderGenericHelper(ConstraintEvalRegistry evalRegistry, C constraint, ComponentBuilder<?, EVAL, ITransformationComponent>.OperationNodesBuilderHelper helper) {
        Class<C> clazz = (Class<C>) constraint.getClass(); //TODO investigate generic casting, this makes me sick...
        ConstraintRelationBuilder<C> constraintRelationBuilder = evalRegistry.getRelationBuilderForConstraint(clazz);

        RelationBuilder<OperationNode<EVAL>> relationBuilder = null;
        if (constraintRelationBuilder != null) {
            relationBuilder = (g, n) -> constraintRelationBuilder.buildConstraintRelation(g, constraint, n);
        }

        return relationBuilder;
    }

    public static void buildConstraintRelation(DepsGraph graph,
                                               ConstraintStruct constraint,
                                               ConstraintEvalRegistry evalRegistry,
                                               OperationNode<? extends ITransformationComponent> constraintOperation) {
        ConstraintRelationBuilder relationBuilder = evalRegistry.getRelationBuilderForConstraint(constraint.getClass());
        relationBuilder.buildConstraintRelation(graph, constraint, constraintOperation);
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

    public static <T extends IConstraintsComponent & IEvaluatedTransformation> void
    childOfConstraint(DepsGraph graph, int constraintIndex, T evaluatedData) {
        ConstraintContainer c = evaluatedData.constraints().getChildren().get(constraintIndex);
        if (c == null || !(c.get() instanceof ChildOfConstraint constraint)) return;

        if (constraint.target.get() == null) return;

        // TODO OMG the constraint passed to this is the not evaluated database struct.
    }




    @FunctionalInterface
    public interface ConstraintRelationBuilder<C extends ConstraintStruct> {
        void buildConstraintRelation(DepsGraph graph, C owner, OperationNode<? extends ITransformationComponent> constraintOperation);
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
