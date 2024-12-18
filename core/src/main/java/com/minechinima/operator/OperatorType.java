package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.ui.elements.UIElement;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public abstract class OperatorType<C extends OperatorContext, T extends OperatorProperties> {
    public final String title;
    public final String description;

    /* flags */
    /**
     * Whether to create an undo step.
     */
    public final boolean pushUndo;
    /**
     * True, if calling {@link Operator#execute(OperatorContext)} with the same properties
     * multiple times will not change anything or cause further necessary operations.
     * <br><br>
     * If this is true {@link Operator#execute(OperatorContext)} will be called everytime a Property is changed
     * in {@link com.minechinima.operator.flags.OpInvokeFlag#RUN_IN_UI} mode.
     */
    public final boolean isRepeatable;
    /**
     * Whether this OperatorType supports the adjust last operation.
     */
    public final boolean supportsAdjustLast;
    /**
     * A set of other operatorTypes that can replace this
     * while in {@link Operator#modal(OperatorContext, CursorInfo)} mode.
     */
    private final Set<OperatorType> switchToTypes = new HashSet<>();

    public OperatorType(String title, String description, boolean pushUndo, boolean isRepeatable, boolean supportsAdjustLast) {
        this.title = title;
        this.description = description;
        this.pushUndo = pushUndo;
        this.isRepeatable = isRepeatable;
        this.supportsAdjustLast = supportsAdjustLast;
    }

    public OperatorType(String title, String description) {
        this.title = title;
        this.description = description;
        this.pushUndo = false;
        this.isRepeatable = false;
        this.supportsAdjustLast = false;
    }

    /**
     * Whether the operator can be executed in the given context.
     * This is declared in the OperatorType to avoid unnecessary instantiation of an Operator.
     * @param context
     * @return false if the Operator cannot be executed.
     * @throws UserException with a meaningful message, if it should be displayed.
     */
    public abstract boolean poll(C context) throws UserException;
    @Nullable
    public UIElement ui(T props) {
        //TODO declare more reusable UIGroup -> allows chaining operators, allows reusing for complex UIs, allows substituting with UIGeneratorFramework
        return null;
    }
    public abstract T createProps();
}
