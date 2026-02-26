package com.minechinima.depsgraph;

import com.minechinima.model.base.BaseStructure;

public class OperationNode<T extends BaseStructure> extends Node {
    private final ComponentNode<T> owner;
    /**
     * Functional programming used here. The only thing that changes
     * is the operation logic, no need for subclasses.
     */
    private final EvalFunction<T> func;
    private final OperationCode key;

    public OperationNode(ComponentNode<T> owner, EvalFunction<T> func, OperationCode key) {
        this.owner = owner;
        this.func = func;
        this.key = key;
    }

    public ComponentNode<T> getOwner() {
        return owner;
    }

    public EvalFunction<T> getFunc() {
        return func;
    }

    public OperationCode getKey() {
        return key;
    }

    @FunctionalInterface
    public interface EvalFunction<T extends BaseStructure> {
        void eval(final DepsGraphManager depsGraph, final T original, final T evaluated);
    }

    /**
     * For identifying a type of Operation, e.g. TRANSFORM_INIT, TRANSFORM_EVAL, TRANSFORM_DONE, BONE_CONSTRAINT
     * <br><br>
     * Advised to implement it in an enum.
     */
    public interface OperationCode {
        String getName();
    }
}
