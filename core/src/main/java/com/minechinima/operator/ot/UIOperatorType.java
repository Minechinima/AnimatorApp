package com.minechinima.operator.ot;

import com.minechinima.operator.UIOperatorContext;
import com.minechinima.operator.ot.commands.IOperator;

/**
 * OperatorType that can be executed while an Operator is running in blocking UI mode.
 */
public abstract class UIOperatorType<O extends IOperator<UIOperatorContext>> extends OperatorType<UIOperatorContext, O> {

    public UIOperatorType(String id, String title, String description, boolean pushUndo) {
        super(id, title, description, pushUndo);
    }

    public UIOperatorType(String id, String title, String description) {
        super(id, title, description);
    }
}
