package com.minechinima.operator.ot;

import com.minechinima.operator.UIOperatorContext;

/**
 * OperatorType that can be executed while an Operator is running in blocking UI mode.
 */
public abstract class UIOperatorType extends OperatorType<UIOperatorContext> {

    public UIOperatorType(String id, String title, String description, int flags) {
        super(id, title, description, flags);
    }

    public UIOperatorType(String id, String title, String description) {
        super(id, title, description);
    }
}
