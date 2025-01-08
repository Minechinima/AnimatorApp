package com.minechinima.operator.ot;

import com.minechinima.operator.UIOperatorContext;

/**
 * OperatorType that can be executed while an Operator is running in blocking UI mode.
 */
public abstract non-sealed class UIOperatorType extends AbstractOperatorType<UIOperatorContext, UIOperatorType> {
    protected UIOperatorType(String title, String description) {
        super(title, description);
    }
}
