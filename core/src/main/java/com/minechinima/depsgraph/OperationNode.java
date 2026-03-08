package com.minechinima.depsgraph;

import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Node represents one logical operation on the data, e.g. a transform operation, a constraint operation, etc.
 * <br><br>
 * The logic of the operation is defined in the functional interface {@link EvalFunction} (functional paradigm).
 * An optional {@link DTOFactory} can be registered to declare specific subdata objects from the evaluated copy
 * that the operation needs. Without a registered factory, the default {@link OperationNodeDTO} is created
 * with the data node's evaluated copy.
 * @param <T>
 */
public class OperationNode<T extends BaseStructure> extends Node {
    private final ComponentNode<T> owner;
    /**
     * Combined evaluator that creates the DTO and calls the eval function.
     * This hides the DTO type parameter internally.
     * <br><br>
     * Can be null if this node is only there for structural reasons (NOOP).
     */
    @Nullable
    private final Evaluator<T> evaluator;
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

    /**
     * Creates an operation node with the default {@link OperationNodeDTO} (only the data node copy).
     * @param func eval function receiving the default DTO, or null for a NOOP node
     */
    public OperationNode(ComponentNode<T> owner, @Nullable EvalFunction<T, OperationNodeDTO<T>> func,
                         OperationCode code, @Nullable String name, @Nullable DataScope scope) {
        this.owner = owner;
        this.evaluator = func == null ? null : (depsGraph, copy) -> func.eval(depsGraph, new OperationNodeDTO<>(copy));
        this.code = code;
        this.name = name == null ? "" : name;
        this.scope = scope;
    }

    /**
     * Creates an operation node with a custom {@link DTOFactory} that declares specific subdata
     * the eval function requires from the evaluated copy.
     * <p>
     * The type parameter {@code D} links the factory and eval function, ensuring type safety
     * so the eval function can infer the concrete DTO type.
     *
     * @param factory creates the DTO from the evaluated copy
     * @param func    eval function receiving the custom DTO
     */
    public <D extends OperationNodeDTO<T>> OperationNode(ComponentNode<T> owner,
                                                          DTOFactory<T, D> factory,
                                                          EvalFunction<T, D> func,
                                                          OperationCode code,
                                                          @Nullable String name,
                                                          @Nullable DataScope scope) {
        this.owner = owner;
        this.evaluator = (depsGraph, copy) -> func.eval(depsGraph, factory.create(copy));
        this.code = code;
        this.name = name == null ? "" : name;
        this.scope = scope;
    }

    public OperationNode(ComponentNode<T> owner, @Nullable EvalFunction<T, OperationNodeDTO<T>> func,
                         OperationCode code, @Nullable String name) {
        this(owner, func, code, name, null);
    }

    public OperationNode(ComponentNode<T> owner, @Nullable EvalFunction<T, OperationNodeDTO<T>> func,
                         OperationCode code) {
        this(owner, func, code, "", null);
    }

    public OperationNode(ComponentNode<T> owner, OperationCode code, String name) {
        this(owner, (EvalFunction<T, OperationNodeDTO<T>>) null, code, name, null);
    }

    public OperationNode(ComponentNode<T> owner, OperationCode code) {
        this(owner, (EvalFunction<T, OperationNodeDTO<T>>) null, code, "", null);
    }

    public ComponentNode<T> getComponent() {
        return owner;
    }

    public DataNode<T> getOwnerDataNode() {
        return this.owner.getOwner();
    }

    @Nullable
    public DataScope getScope() {
        return scope;
    }

    /**
     * Evaluates this operation by creating a DTO from the evaluated copy and invoking the eval function.
     * Does nothing for NOOP nodes.
     *
     * @param depsGraph the dependency graph
     * @param copy      the evaluated copy of the data node (never the original)
     */
    public void evaluate(DepsGraph depsGraph, T copy) {
        if (this.evaluator != null) {
            this.evaluator.evaluate(depsGraph, copy);
        }
    }

    /**
     * @return true, if this is a NOOP (no operation) node,
     * which means it has no logic and is only there for structural reasons, e.g. for building relations to a specific point in the component.
     */
    public boolean isNOOP() {
        return this.evaluator == null;
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
     * The logic of the operation. Receives an {@link OperationNodeDTO} that provides
     * the evaluated copy and any declared subdata. Operation nodes never receive the
     * original database objects.
     * <br><br>
     * Allows to use static functions, decouple logic fully from runtime data structures.
     *
     * @param <T> the data node's data type
     * @param <D> the DTO type, defaults to {@link OperationNodeDTO} when no custom DTO is registered
     */
    @FunctionalInterface
    public interface EvalFunction<T extends BaseStructure, D extends OperationNodeDTO<T>> {
        void eval(final DepsGraph depsGraph, final D dto);
    }

    /**
     * Factory that creates an {@link OperationNodeDTO} from the evaluated copy.
     * Custom factories can extract specific subdata from the copy and expose them
     * through a typed DTO subclass.
     *
     * @param <T> the data node's data type
     * @param <D> the concrete DTO type this factory produces
     */
    @FunctionalInterface
    public interface DTOFactory<T extends BaseStructure, D extends OperationNodeDTO<T>> {
        D create(T copy);
    }

    /**
     * Internal evaluator that type-erases the DTO parameter.
     * Created by combining a {@link DTOFactory} and an {@link EvalFunction}.
     */
    @FunctionalInterface
    interface Evaluator<T extends BaseStructure> {
        void evaluate(DepsGraph depsGraph, T copy);
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
