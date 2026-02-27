package com.minechinima.depsgraph;

import com.minechinima.depsgraph.builder.RelationBuilder;
import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Node represents one logical operation on the data, e.g. a transform operation, a constraint operation, etc.
 * <br><br>
 * The logic of the operation is defined in the functional interface {@link EvalFunction} (functional paradigm)
 * @param <T>
 */
public class OperationNode<T extends BaseStructure> extends Node {
    private final ComponentNode<T> owner;
    /**
     * Functional programming used here. The only thing that changes
     * is the operation logic, no need for subclasses.
     * <br><br>
     * Can be null if this node is only there for structural reasons.
     */
    @Nullable
    private final EvalFunction<T> func;
    private final OperationCode code;
    /**
     * Name of this operation to differentiate between operations with the same code,
     * e.g. for constraints: code is Constraint and name is
     * the name or index of the operation. Can be empty if not needed;
     */
    private final String name;

    /**
     * For scheduling the execution of the operations.
     * Only executes when all incoming relations are fulfilled.
     */
    private int linksPending = 0;
    private boolean scheduled;
    /**
     * @implNote final to ensure data has 1-1 mapping with nodes.
     */
    @Nullable
    private final DataScope scope;

    public OperationNode(ComponentNode<T> owner, EvalFunction<T> func, OperationCode code, @Nullable String name, @Nullable DataScope scope) {
        this.owner = owner;
        this.func = func;
        this.code = code;
        this.name = name == null ? "" : name;
        this.scope = scope;
    }

    public OperationNode(ComponentNode<T> owner, EvalFunction<T> func, OperationCode code, @Nullable String name) {
        this(owner, func, code, "", null);
    }

    public OperationNode(ComponentNode<T> owner, EvalFunction<T> func, OperationCode code) {
        this(owner, func, code, "");
    }

    public OperationNode(ComponentNode<T> owner, OperationCode code, String name) {
        this(owner, null, code, name);
    }

    public OperationNode(ComponentNode<T> owner, OperationCode code) {
        this(owner, null, code, "");
    }

    public ComponentNode<T> getComponent() {
        return owner;
    }

    public DataNode<T> getOwnerDataNode() {
        return this.owner.getOwner();
    }

    public EvalFunction<T> getFunc() {
        return func;
    }

    @Nullable
    public DataScope getScope() {
        return scope;
    }

    /**
     * @return true, if this is a NOOP (no operation) node,
     * which means it has no logic and is only there for structural reasons, e.g. for building relations to a specific point in the component.
     */
    public boolean isNOOP() {
        return this.func == null;
    }

    public OperationCode getCode() {
        return code;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return this.code.getName() + (this.name.isEmpty() ? "" : "_" + this.name);
    }

    @Override
    public @NotNull OperationNode<?> getEntry() {
        return this.owner.getEntry();
    }

    @Override
    public @NotNull OperationNode<?> getExit() {
        return this.owner.getExit();
    }

    /**
     * The logic of the operation. Evaluates the original data structure and writes the result to the evaluated data structure.
     * Allows to use static functions, decouple logic fully from runtime data structures.
     * @param <T>
     */
    @FunctionalInterface
    public interface EvalFunction<T extends BaseStructure> {
        void eval(final DepsGraph depsGraph, final T original, final T evaluated);
    }

    /**
     * For identifying a type of Operation, e.g. TRANSFORM_ENTRY, TRANSFORM_EVAL, TRANSFORM_EXIT, BONE_CONSTRAINT
     * <br><br>
     * Advised to implement it in an enum.
     */
    public interface OperationCode {
        String getName();
    }
}
